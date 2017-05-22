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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;


/**
 * 加密算法RSA工具类
 * @version 1.0.0
 */
public class RsaHelper
{
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成RSA密钥对(默认密钥长度为1024)
     *
     * @return KeyPair
     */
    public static KeyPair generateRSAKeyPair()
    {
        return generateRSAKeyPair(1024);
    }

    /**
     * 生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048
     * @return KeyPair
     */
    public static KeyPair generateRSAKeyPair(int keyLength)
    {
        try {
//			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA/ECB/PKCS1Padding");
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    public static String encode64PublicKeyString(PublicKey key) throws Exception
    {
//		RSAPublicKey rsaPublickey = (RSAPublicKey)key;
        byte[] keyBytes = key.getEncoded();

        String s = Base64.getEncoder().encodeToString(keyBytes);
        return s;
    }

    public static String encode64PrivateKeyString(PrivateKey key) throws Exception
    {
//		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)key;
        byte[] keyBytes = key.getEncoded();
        String s = Base64.getEncoder().encodeToString(keyBytes);
        return s;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @return PublicKey
     * @throws Exception PublicKey
     * @since 1.0.0
     */
    public static PublicKey decodePublicKeyFromBase64Str(String key) throws Exception
    {
        byte[] keyBytes;
        keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @return PrivateKey
     * @throws Exception PrivateKey
     * @since 1.0.0
     */
    public static PrivateKey decodePrivateKeyFromBase64Str(String key) throws Exception
    {
        byte[] keyBytes;
        keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * java端公钥转换成C#公钥
     *
     * @param key PublicKey
     * @return String
     * @since 1.0.0
     */
    public static String encodePublicKeyToXml(PublicKey key)
    {
        if (!RSAPublicKey.class.isInstance(key)) {
            return null;
        }
        RSAPublicKey pubKey = (RSAPublicKey) key;
        StringBuilder sb = new StringBuilder();

        sb.append("<RSAKeyValue>");
        sb.append("<Modulus>").append(Base64.getEncoder().encodeToString(pubKey.getModulus().toByteArray())).append("</Modulus>");
        sb.append("<Exponent>").append(Base64.getEncoder().encodeToString(pubKey.getPublicExponent().toByteArray())).append("</Exponent>");
        sb.append("</RSAKeyValue>");
        return sb.toString();
    }

    /**
     * C#端公钥转换成java公钥
     *
     * @param xml String
     * @return PublicKey
     * @since 1.0.0
     */
    public static PublicKey decodePublicKeyFromXml(String xml)
    {
        xml = xml.replaceAll("\r", "").replaceAll("\n", "");
        BigInteger modulus = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<Modulus>", "</Modulus>")));
        BigInteger publicExponent = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<Exponent>", "</Exponent>")));
        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent);
        KeyFactory keyf;
        try {
            keyf = KeyFactory.getInstance("RSA");
            return keyf.generatePublic(rsaPubKey);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * C#端私钥转换成java私钥
     *
     * @param xml String
     * @return PrivateKey
     * @since 1.0.0
     */
    public static PrivateKey decodePrivateKeyFromXml(String xml)
    {
        xml = xml.replaceAll("\r", "").replaceAll("\n", "");
        BigInteger modulus = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<Modulus>", "</Modulus>")));
        BigInteger publicExponent = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<Exponent>", "</Exponent>")));
        BigInteger privateExponent = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<D>", "</D>")));
        BigInteger primeP = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<P>", "</P>")));
        BigInteger primeQ = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<Q>", "</Q>")));
        BigInteger primeExponentP = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<DP>", "</DP>")));
        BigInteger primeExponentQ = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<DQ>", "</DQ>")));
        BigInteger crtCoefficient = new BigInteger(1, Base64.getDecoder().decode(RsaStringUtils.getMiddleString(xml, "<InverseQ>", "</InverseQ>")));

        RSAPrivateCrtKeySpec rsaPriKey = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
        KeyFactory keyf;
        try {
            keyf = KeyFactory.getInstance("RSA");
            return keyf.generatePrivate(rsaPriKey);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * java端私钥转换成C#私钥
     *
     * @param key PrivateKey
     * @return String
     * @since 1.0.0
     */
    public static String encodePrivateKeyToXml(PrivateKey key)
    {
        if (!RSAPrivateCrtKey.class.isInstance(key)) {
            return null;
        }
        RSAPrivateCrtKey priKey = (RSAPrivateCrtKey) key;
        StringBuilder sb = new StringBuilder();

        sb.append("<RSAKeyValue>");
        sb.append("<Modulus>").append(Base64.getEncoder().encodeToString(priKey.getModulus().toByteArray())).append("</Modulus>");
        sb.append("<Exponent>").append(Base64.getEncoder().encodeToString(priKey.getPublicExponent().toByteArray())).append("</Exponent>");
        sb.append("<P>").append(Base64.getEncoder().encodeToString(priKey.getPrimeP().toByteArray())).append("</P>");
        sb.append("<Q>").append(Base64.getEncoder().encodeToString(priKey.getPrimeQ().toByteArray())).append("</Q>");
        sb.append("<DP>").append(Base64.getEncoder().encodeToString(priKey.getPrimeExponentP().toByteArray())).append("</DP>");
        sb.append("<DQ>").append(Base64.getEncoder().encodeToString(priKey.getPrimeExponentQ().toByteArray())).append("</DQ>");
        sb.append("<InverseQ>").append(Base64.getEncoder().encodeToString(priKey.getCrtCoefficient().toByteArray())).append("</InverseQ>");
        sb.append("<D>").append(Base64.getEncoder().encodeToString(priKey.getPrivateExponent().toByteArray())).append("</D>");
        sb.append("</RSAKeyValue>");
        return sb.toString();
    }

    /**
     * 用公钥加密
     *
     * @param data byte[]
     * @param pubKey PublicKey
     * @return byte[]
     * @since 1.0.0
     */
    public static byte[] encryptData(byte[] data, PublicKey pubKey)
    {
        try {
            /*Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			return cipher.doFinal(data);*/
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                // 写入内存缓冲区
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            // 获取内存缓冲中的数据，转换成数组
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData byte[]
     * @param priKey PrivateKey
     * @return byte[]
     * @since 1.0.0
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey priKey)
    {
        try {
			/*Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			return cipher.doFinal(encryptedData);*/
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                // 写入内存缓冲区
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            // 获取内存缓冲中的数据，转换成数组
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据指定公钥进行明文加密
     *
     * @param plainText 要加密的明文数据
     * @param pubKey    公钥
     * @return String
     * @since 1.0.0
     */
    public static String encryptDataFromStr(String plainText, PublicKey pubKey)
    {

        try {
            byte[] dataByteArray = plainText.getBytes("gb2312");
            byte[] encryptedDataByteArray = RsaHelper.encryptData(
                    dataByteArray, pubKey);
            return Base64.getEncoder().encodeToString(encryptedDataByteArray);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据指定私钥对数据进行签名(默认签名算法为"SHA1withRSA")
     *
     * @param data   要签名的数据
     * @param priKey 私钥
     * @return byte[]
     * @since 1.0.0
     */
    public static byte[] signData(byte[] data, PrivateKey priKey)
    {
        return signData(data, priKey, "SHA1withRSA");
    }

    /**
     * 根据指定私钥和算法对数据进行签名
     *
     * @param data      要签名的数据
     * @param priKey    私钥
     * @param algorithm 签名算法
     * @return byte[]
     * @since 1.0.0
     */
    public static byte[] signData(byte[] data, PrivateKey priKey,
                                  String algorithm)
    {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 用指定的公钥进行签名验证(默认签名算法为"SHA1withRSA")
     *
     * @param data   数据
     * @param sign   签名结果
     * @param pubKey 公钥
     * @return boolean
     * @since 1.0.0
     */
    public static boolean verifySign(byte[] data, byte[] sign, PublicKey pubKey)
    {
        return verifySign(data, sign, pubKey, "SHA1withRSA");
    }

    /**
     * 用指定的公钥进行签名验证
     *
     * @param data      数据
     * @param sign      签名结果
     * @param pubKey    公钥
     * @param algorithm 签名算法
     * @return boolean
     * @since 1.0.0
     */
    public static boolean verifySign(byte[] data, byte[] sign,
                                     PublicKey pubKey, String algorithm)
    {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception ex) {
            return false;
        }
    }

}
