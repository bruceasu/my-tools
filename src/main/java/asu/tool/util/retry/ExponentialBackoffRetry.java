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


import java.util.Random;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Retry policy that retries a set number of times with increasing sleep time
 * between retries
 */
public class ExponentialBackoffRetry extends SleepingRetry
{
    private static final Log log = Logs.get();

    private static final int MAX_RETRIES_LIMIT = 29;
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;

    private final Random random = new Random();
    private final int baseSleepTimeMs;
    private final int maxSleepMs;

    /**
     * @param baseSleepTimeMs
     *         initial amount of time to wait between retries
     * @param maxRetries
     *         max number of times to retry
     */
    public ExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries)
    {
        this(baseSleepTimeMs, maxRetries, DEFAULT_MAX_SLEEP_MS);
    }

    /**
     * @param baseSleepTimeMs
     *         initial amount of time to wait between retries
     * @param maxRetries
     *         max number of times to retry
     * @param maxSleepMs
     *         max time in ms to sleep on each retry
     */
    public ExponentialBackoffRetry(int baseSleepTimeMs,
                                   int maxRetries,
                                   int maxSleepMs)
    {
        super(validateMaxRetries(maxRetries));
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxSleepMs = maxSleepMs;
    }

    public int getBaseSleepTimeMs()
    {
        return baseSleepTimeMs;
    }

    @Override
    protected long getSleepTimeMs(int retryCount, long elapsedTimeMs)
    {
        // copied from Hadoop's RetryPolicies.java
        long sleepMs = baseSleepTimeMs * Math.max(1,
                random.nextInt(1 << (retryCount + 1)));
        if (sleepMs > maxSleepMs) {
            log.warn(String.format(
                    "Sleep extension too large (%d). Pinning to %d",
                    sleepMs, maxSleepMs));
            sleepMs = maxSleepMs;
        }
        return sleepMs;
    }

    private static int validateMaxRetries(int maxRetries)
    {
        if (maxRetries > MAX_RETRIES_LIMIT) {
            log.warn(String.format("maxRetries too large (%d). Pinning to %d",
                    maxRetries, MAX_RETRIES_LIMIT));
            maxRetries = MAX_RETRIES_LIMIT;
        }
        return maxRetries;
    }
}