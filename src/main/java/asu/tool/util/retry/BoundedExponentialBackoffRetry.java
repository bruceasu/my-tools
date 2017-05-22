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


/**
 * Retry policy that retries a set number of times with an increasing (up to a maximum bound) sleep time between retries
 */
public class BoundedExponentialBackoffRetry extends ExponentialBackoffRetry
{
    private final int maxSleepTimeMs;

    /**
     * @param baseSleepTimeMs initial amount of time to wait between retries
     * @param maxSleepTimeMs maximum amount of time to wait between retries
     * @param maxRetries maximum number of times to retry
     */
    public BoundedExponentialBackoffRetry(int baseSleepTimeMs,
                                          int maxSleepTimeMs,
                                          int maxRetries)
    {
        super(baseSleepTimeMs, maxRetries);
        this.maxSleepTimeMs = maxSleepTimeMs;
    }

    public int getMaxSleepTimeMs()
    {
        return maxSleepTimeMs;
    }

    @Override
    protected long getSleepTimeMs(int retryCount, long elapsedTimeMs)
    {
        return Math.min(maxSleepTimeMs, super.getSleepTimeMs(retryCount, elapsedTimeMs));
    }
}
