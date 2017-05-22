/*
 * Copyright © 2016 Victor.su<victor.su@gwtsz.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package asu.tool.util.retry;

import asu.tool.util.DefaultTracerDriver;
import asu.tool.util.TracerDriver;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * <p>Mechanism to perform an operation on Job that is safe against
 * disconnections and "recoverable" errors.</p>
 * <p> If an exception occurs during the operation, the RetryLoop will
 * process it, check with the current retry policy and either attempt to
 * reconnect or re-throw the exception </p>
 *
 * Canonical usage:<br>
 * <pre>
 * RetryLoop retryLoop = new RetryLoop(...);
 * while ( retryLoop.shouldContinue() )
 * {
 *     try {
 *         // do your work
 *         // it's important to re-get the instance in case there was an error
 *         // and the instance was re-created
 *
 *         retryLoop.markComplete();
 *     } catch ( Exception e ) {
 *         retryLoop.takeException(e);
 *     }
 * }
 * </pre>
 */
public class RetryLoop
{
    private boolean isDone = false;
    private int retryCount = 0;

    private final Log log = Logs.get();
    private final long startTimeMs = System.currentTimeMillis();
    private final RetryPolicy retryPolicy;
    private final AtomicReference<? extends TracerDriver> tracer;

    public final Set<Class<? extends Throwable>> allowRetryException = new HashSet<>();

    private static final RetrySleeper sleeper = RetrySleeper.create();

    /**
     * Returns the default retry sleeper
     *
     * @return sleeper
     */
    public static RetrySleeper getDefaultRetrySleeper()
    {
        return sleeper;
    }

    /**
     * Convenience utility: creates a retry loop calling the given proc and
     * retrying if needed
     *
     * @param proc            procedure to call with retry
     * @param retryPolicy     RetryPolicy
     * @param tracer          TracerDriver
     * @param retryAbleThrows throwable array
     * @param <T>             return type
     * @return procedure result
     * @throws Exception any non-retriable errors
     */
    public static <T> T callWithRetry(Callable<T> proc,
                                      RetryPolicy retryPolicy,
                                      AtomicReference<? extends TracerDriver> tracer,
                                      Class<? extends Throwable>... retryAbleThrows
    ) throws Exception
    {
        T result = null;
        RetryLoop retryLoop = new RetryLoop(retryPolicy, tracer);
        if (retryAbleThrows != null && retryAbleThrows.length > 0) {
            retryLoop.allowRetryException.addAll(Arrays.asList(retryAbleThrows));
        }
        while (retryLoop.shouldContinue()) {
            try {
                result = proc.call();
                retryLoop.markComplete();
            } catch (Exception e) {
                checkInterrupted(e);
                retryLoop.takeException(e);
            }
        }
        return result;
    }

    public static <T> T callWithRetry(Callable<T> proc,
                                      RetryPolicy retryPolicy,
                                      Class<? extends Throwable>... retryAbleThrows)
            throws Exception
    {
        TracerDriver td = new DefaultTracerDriver();
        return callWithRetry(proc, retryPolicy,
                new AtomicReference<TracerDriver>(td), retryAbleThrows);
    }

    public static <T> T callWithRetry(Callable<T> proc,
                                      RetryPolicy retryPolicy) throws Exception
    {
        TracerDriver td = new DefaultTracerDriver();
        return callWithRetry(proc, retryPolicy, new AtomicReference<TracerDriver>(td));
    }

    public static <T> T callWithRetry(
            Callable<T> proc, Class<? extends Throwable>... retryAbleThrows)
            throws Exception
    {
        RetryPolicy retryPolicy = new RetryForever(1000);
        TracerDriver td = new DefaultTracerDriver();
        return callWithRetry(proc, retryPolicy, new AtomicReference<TracerDriver>(td), retryAbleThrows);
    }

    public static <T> T callWithRetry(Callable<T> proc) throws Exception
    {
        RetryPolicy retryPolicy = new RetryForever(1000);
        TracerDriver td = new DefaultTracerDriver();
        return callWithRetry(proc, retryPolicy, new AtomicReference<TracerDriver>(td));
    }

    private static void checkInterrupted(Throwable e)
    {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }
    // new AtomicReference<TracerDriver>(new DefaultTracerDriver());

    public RetryLoop(RetryPolicy retryPolicy, AtomicReference<? extends TracerDriver> tracer)
    {
        this.retryPolicy = retryPolicy;
        this.tracer = tracer;
    }

    /**
     * If true is returned, make an attempt at the operation
     *
     * @return true/false
     */
    public boolean shouldContinue()
    {
        return !isDone;
    }

    /**
     * Call this when your operation has successfully completed
     */
    public void markComplete()
    {
        isDone = true;
    }

    /**
     * Pass any caught exceptions here
     *
     * @param exception the exception
     * @throws Exception if not retry-able or the retry policy returned negative
     */
    public void takeException(Exception exception) throws Exception
    {
        boolean rethrow = true;
        if (isRetryException(exception)) {
            log.debug("Retry-able exception received", exception);

            if (retryPolicy.allowRetry(retryCount++, System.currentTimeMillis() - startTimeMs, sleeper)) {
                tracer.get().addCount("retries-allowed", 1);
                log.debug("Retrying operation");
                rethrow = false;
            } else {
                tracer.get().addCount("retries-disallowed", 1);
                log.debug("Retry policy not allowing retry");
            }
        }

        if (rethrow) {
            throw exception;
        }
    }


    /**
     * Utility - return true if the given exception is retry-able
     *
     * @param exception exception to check
     * @return true/false
     */
    protected boolean isRetryException(Throwable exception)
    {
        return allowRetryException.contains(exception.getClass());
    }


}

