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

/**
 * 加密算法RSA工具类
 *
 * @version 1.0.0
 */
public class RsaStringUtils
{

    //system root path
    public static String sysRootPath = "";

    /**
     * 获取当前项目的根目录
     *
     * @return 系统目录名
     * @version 1.1
     */
    public static String getPath()
    {
        if ("".equals(sysRootPath)) {
            RsaStringUtils o = new RsaStringUtils();
            String projectPath = o.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            String path = projectPath.substring(0, projectPath.lastIndexOf("/") - 1);
            sysRootPath = path.substring(0, path.lastIndexOf("/"));
        }
        return sysRootPath;

    }

    /**
     * 返回两个字符串中间的内容
     *
     * @param all String
     * @param start String
     * @param end String
     * @return String
     */
    public static String getMiddleString(String all, String start, String end)
    {
        int beginIdx = all.indexOf(start) + start.length();
        int endIdx = all.indexOf(end);
        return all.substring(beginIdx, endIdx);
    }
}