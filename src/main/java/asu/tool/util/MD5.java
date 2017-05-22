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


import java.security.MessageDigest;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * MD5加密解密工具类
 *
 * @author darren.qiu
 * @version 1.0.0
 * @date 2016年3月25日 上午10:10:11
 */
public class MD5 {
  private static final Log log = Logs.get();

  public static String encodeByMD5(String source) {
    String s = null;
    char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
        'd', 'e', 'f'};
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(source.getBytes());
      byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
      // 用字节表示就是 16 个字节
      char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
      // 所以表示成 16 进制需要 32 个字符
      int k = 0; // 表示转换结果中对应的字符位置
      for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
        // 转换成 16 进制字符的转换
        byte byte0 = tmp[i]; // 取第 i 个字节
        str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
        // >>>
        // 为逻辑右移，将符号位一起右移
        str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
      }
      s = new String(str); // 换后的结果转换为字符串

    } catch (Exception e) {
      log.error(e);
    }
    return s;
  }

  public static String encodeByMD5(byte[] source) {
    String s = null;
    char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
        'd', 'e', 'f'};
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(source);
      byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
      // 用字节表示就是 16 个字节
      char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
      // 所以表示成 16 进制需要 32 个字符
      int k = 0; // 表示转换结果中对应的字符位置
      for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
        // 转换成 16 进制字符的转换
        byte byte0 = tmp[i]; // 取第 i 个字节
        str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
        // >>>
        // 为逻辑右移，将符号位一起右移
        str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
      }
      s = new String(str); // 换后的结果转换为字符串

    } catch (Exception e) {
      log.error("error", e);
    }
    return s;
  }

  /**
   * 验证输入的密码是否正确
   *
   * @param password    加密后的密码
   * @param inputString 输入的字符串
   * @return 验证结果，TRUE:正确 FALSE:错误
   */
  public static boolean validatePassword(String password, String inputString) {
    if (password.equals(encodeByMD5(inputString))) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 验证输入的密码是否正确
   *
   * @param password    加密后的密码
   * @param inputString 输入的字符串
   * @return 验证结果，TRUE:正确 FALSE:错误
   */
  public static boolean validatePassword(String password, byte[] inputString) {
    if (password.equals(encodeByMD5(inputString))) {
      return true;
    } else {
      return false;
    }
  }

}
