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

import java.io.UnsupportedEncodingException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ED {
  private static final Log log = Logs.get();
  private String key = "test_key";
  private boolean compressable = false;
  private boolean encryptable = true;


  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setCompressable(boolean compressable) {
    this.compressable = compressable;
  }

  public boolean getCompressable() {
    return compressable;
  }

  public void setEncryptable(boolean encryptable) {
    this.encryptable = encryptable;
  }

  public boolean getEncryptable() {
    return encryptable;
  }

  public byte[] encryptFromString(String plainXml) throws Exception {
    return Blowfish.encrypt(Strings.getBytesUTF8(plainXml), getKey());
  }

  public String decryptToString(byte[] encrypted) throws Exception {
    byte[] bytes = Blowfish.decrypt(encrypted, getKey());
    try {
      return new String(bytes, "utf-8");
    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 not supported?", e);
      return null;
    }
  }

  public byte[] encrypt(byte[] plainXml) throws Exception {
    return Blowfish.encrypt(plainXml, getKey());
  }

  public byte[] decrypt(byte[] encrypted) throws Exception {
    return Blowfish.decrypt(encrypted, getKey());
  }
}
