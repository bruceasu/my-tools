package asu.tool.util;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.UnsupportedEncodingException;

/**
 * ${document}
 *
 * @author <a href="mailto:victor.su@gwtsz.net">Victor Su&lt;victor.su@gwtsz.net&gt;</a>
 * @version 1.0.0
 * @date 2016/7/11 11:38
 * @copyright 2016 Victor All rights reserved.
 * @since ${since}
 */
public class Hex {
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final String charsetName;

    public static byte[] decodeHex(char[] data) throws DecoderException {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new DecoderException("Odd number of characters.");
        } else {
            byte[] out = new byte[len >> 1];
            int i = 0;

            for (int j = 0; j < len; ++i) {
                int f = toDigit(data[j], j) << 4;
                ++j;
                f |= toDigit(data[j], j);
                ++j;
                out[i] = (byte) (f & 255);
            }

            return out;
        }
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }

        return out;
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    protected static int toDigit(char ch, int index) throws DecoderException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
        } else {
            return digit;
        }
    }

    public Hex() {
        this.charsetName = "UTF-8";
    }

    public Hex(String csName) {
        this.charsetName = csName;
    }

    public byte[] decode(byte[] array) throws DecoderException {
        try {
            return decodeHex((new String(array, this.getCharsetName())).toCharArray());
        } catch (UnsupportedEncodingException var3) {
            throw new DecoderException(var3.getMessage(), var3);
        }
    }

    public byte[] decode(Object object) throws DecoderException {
        try {
            char[] e = object instanceof String ? ((String) object).toCharArray() : (char[]) ((char[]) object);
            return decodeHex(e);
        } catch (ClassCastException var3) {
            throw new DecoderException(var3.getMessage(), var3);
        }
    }

    public byte[] encode(byte[] array) {
        return Bytes.toBytes(encodeHexString(array));
    }

    /**
     *
     * @param object String or byte[]
     * @return char[]
     * @throws EncoderException ClassCastException or UnsupportedEncodingException
     */
    public char[] encode(Object object) throws EncoderException {
        try {
            byte[] e = object instanceof String ? ((String) object).getBytes(this.getCharsetName()) : (byte[]) ((byte[]) object);
            return encodeHex(e);
        } catch (ClassCastException var3) {
            throw new EncoderException(var3.getMessage(), var3);
        } catch (UnsupportedEncodingException var4) {
            throw new EncoderException(var4.getMessage(), var4);
        }
    }

    public String getCharsetName() {
        return this.charsetName;
    }

    public String toString() {
        return super.toString() + "[charsetName=" + this.charsetName + "]";
    }
}
