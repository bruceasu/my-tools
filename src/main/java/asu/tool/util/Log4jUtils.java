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

package asu.tool.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jUtils
{
    private static Map<String, Level> cache = new HashMap<>();

    public static void setDBDebug() {
        setLevel("java.sql", Level.DEBUG);
    }

    public static void resetDBLevel() {
        reset("java.sql");
    }

    public static void setLevel(String logger, Level level) {
        Level level1 = getLevel(logger);
        cache.put(logger, level1);
        getLogger(logger).setLevel(level);
    }

    public static void setLevel(String logger, String level) {
        Level level1 = getLevel(logger);
        cache.put(logger, level1);
        getLogger(logger).setLevel(Level.toLevel(level));
    }

    public static void reset(String logger) {
        if (cache.containsKey(logger)) {
            Level level = cache.get(logger);
            getLogger(logger).setLevel(level);
            cache.remove(logger);
        }
    }

    public static Logger getLogger(String logger) {
        return Logger.getLogger(logger);
    }

    public static Level getLevel(String logger) {
        return Logger.getLogger(logger).getLevel();
    }
}
