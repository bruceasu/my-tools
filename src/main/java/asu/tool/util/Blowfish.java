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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.nutz.lang.Strings;

public class Blowfish {

  public static byte[] encrypt(byte[] message, String key) throws Exception {
    SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "Blowfish");
//        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
    Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    int blockSize = cipher.getBlockSize();
    int mod = message.length % blockSize;
    int len = message.length;
    if (mod > 0) {
      len = message.length - mod + blockSize;
      byte[] tempMsg = new byte[len];
      System.arraycopy(message, 0, tempMsg, 0, message.length);
      message = tempMsg;
    }
    byte[] encryptedMsg = cipher.doFinal(message);
    byte[] result = new byte[len];
    System.arraycopy(encryptedMsg, 0, result, 0, len);
    return result;
  }

  public static byte[] decrypt(byte[] message, String key) throws Exception {
    SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "Blowfish");
    Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, secret);
    byte[] decryptedMsg = cipher.doFinal(message);
    int lastPos = decryptedMsg.length;
    for (int i = decryptedMsg.length - 1; i >= 0; i--) {
      if (decryptedMsg[i] == 0) {
        lastPos = i;
      } else {
        break;
      }
    }
    byte[] result = new byte[lastPos];
    System.arraycopy(decryptedMsg, 0, result, 0, result.length);
    return result;
  }

//    public static byte[] decryptBlowfish(byte[] message, String key) throws Exception {
//        SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "Blowfish");
//        Cipher cipher = Cipher.getInstance("Blowfish");
//        cipher.init(Cipher.DECRYPT_MODE, secret);
//        byte[] end = getBlowfishVerifyCode(key);
//        byte[] tempMsg = new byte[message.length + cipher.getBlockSize()];
//        System.arraycopy(message, 0, tempMsg, 0, message.length);
//        System.arraycopy(end, 0, tempMsg, message.length, end.length);
//        byte[] decryptedMsg = cipher.doFinal(tempMsg);
//        int lastPos = decryptedMsg.length;
//        for (int i = decryptedMsg.length - 1; i >= 0; i--) {
//            if (decryptedMsg[i] == 0) {
//                lastPos = i;
//            } else {
//                break;
//            }
//        }
//        byte[] result = new byte[lastPos];
//        System.arraycopy(decryptedMsg, 0, result, 0, result.length);
//        return result;
//    }
//    private static byte[] getBlowfishVerifyCode(String key) throws Exception {
//        SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "Blowfish");
//        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
//        byte[] tempMsg = new byte[0];
////        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
////        byte[] tempMsg = new byte[]{0x08,0x08,0x08,0x08,0x08,0x08,0x08,0x08};
//        cipher.init(Cipher.ENCRYPT_MODE, secret);
//
//        return cipher.doFinal(tempMsg);
//    }

  private static String byte2hex(byte[] b) {
    StringBuilder hs = new StringBuilder();
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = (Integer.toHexString(b[n] & 0XFF));
      if (stmp.length() == 1)
        hs.append("0").append(stmp);
      else
        hs.append(stmp);
    }
    return hs.toString();
  }

  public static void main(String[] args) throws Exception {
    String test = "123456789";
    byte[] message = test.getBytes();
    String key = "1234567890";
    byte[] bytes, bytes1;

    System.out.println("origin test string : " + test);

//        System.out.println(Strings.dup('-', 60));
//        byte[] bytes = encrypt(message, key);
//        System.out.println("encrypt to HEX = " + byte2hex(bytes));
//        byte[] bytes1 = decryptBlowfish(bytes, key);
//        System.out.println("decrypt : " + new String(bytes1));

    System.out.println(Strings.dup('-', 60));
    bytes = encrypt(message, key);
    System.out.println("encrypt to HEX = " + byte2hex(bytes));
    bytes1 = decrypt(bytes, key);
    System.out.println("decrypt : " + new String(bytes1));
  }


}
