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

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.nutz.lang.Strings;

/**
 * 加密算法RSA工具类
 *
 * @version 1.0.0
 */
public class RSAUtil
{
    private static Logger logger = Logger.getLogger(RSAUtil.class);
    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    /** */
    /**
     * 获取公钥的key
     */
    public static final String PUBLIC_KEY = "RSAPublicKey";
    /** */
    /**
     * 获取私钥的key
     */
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    public static final String QSTRING_EQUAL = "=";
    public static final String QSTRING_SPLIT = "&";
    //SIGN
    public final static String SIGNATURE = "sign";
    public final static String RSCODE = "rsCode";
    public final static String RSMEG = "rsMsg";

    /**
     * 生成字符串类型的公钥、私钥对
     *
     * @return Map
     * @throws Exception Exception
     * @since 1.0.0
     */
    public static Map<String, String> generateKeys() throws Exception
    {
        Map<String, String> keysMap = new HashMap<String, String>();
        KeyPair kp = RsaHelper.generateRSAKeyPair();
        PublicKey pubKey = kp.getPublic();
        PrivateKey priKey = kp.getPrivate();
        String pubKeyXml = RsaHelper.encodePublicKeyToXml(pubKey);
        String priKeyXml = RsaHelper.encodePrivateKeyToXml(priKey);
        //String pubKeyXml = RsaHelper.encode64PublicKeyString(pubKey);
        //String priKeyXml = RsaHelper.encode64PrivateKeyString(priKey);
        keysMap.put(RSAUtil.PUBLIC_KEY, pubKeyXml);
        keysMap.put(RSAUtil.PRIVATE_KEY, priKeyXml);
        return keysMap;
    }

    /**
     * 公钥加密
     *
     * @param sourceData 源数据
     * @param publicKey  公钥(BASE64编码)
     * @return String
     * @throws Exception String
     * @since 1.0.0
     */
    public static String encryptByPublicKey(String sourceData, String publicKey) throws Exception
    {

        PublicKey publicKeyObj = RsaHelper.decodePublicKeyFromXml(publicKey);
        //PublicKey publicKeyObj = RsaHelper.decodePublicKeyFromBase64Str(publicKey);
        byte[] sourceDataByteArray = sourceData.getBytes("utf-8");
        byte[] encodeDataByteArray = RsaHelper.encryptData(sourceDataByteArray, publicKeyObj);
        return Base64.getEncoder().encodeToString(encodeDataByteArray);
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return String
     * @throws Exception String
     * @since 1.0.0
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKey) throws Exception
    {
        PrivateKey privateKeyObj = RsaHelper.decodePrivateKeyFromXml(privateKey);
        //PrivateKey privateKeyObj = RsaHelper.decodePrivateKeyFromBase64Str(privateKey);
        byte[] encryptedDataArray = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedDataByteArray = RsaHelper.decryptData(encryptedDataArray, privateKeyObj);
        if (decryptedDataByteArray == null) return null;
        String decryptedData = new String(decryptedDataByteArray, "utf-8");
        return decryptedData;
    }

    /**
     * 公钥加密
     *
     * @param data      待加密Map
     * @param publicKey 公钥
     * @return String
     * @throws Exception String
     * @since 1.0.0
     */
    public static String publicRequestEncrypt(Map<String, String> data, String publicKey) throws Exception
    {
        String nvp = buildReq(data, false);
        logger.info("【待签名字符串】" + nvp);
        return encryptByPublicKey(nvp, publicKey);
    }

    /**
     * 验密
     *
     * @param paramMap Map
     * @param privateKey 私钥
     * @return boolean
     * @since 1.0.0
     */
    public static boolean privateDecrypt(Map<String, String> paramMap, String privateKey)
    {
        // 获取请求参数中的签名域
        String sign = paramMap.get(SIGNATURE);
        logger.info("【请求验签|请求签名域】" + sign);
        if (Strings.isEmpty(sign)) {
            return false;
        }
        // 拼接请求字符串
        String nvp = buildReq(paramMap, false);
        logger.info("【请求验签|签名参数】" + nvp);
        try {
            return nvp.equalsIgnoreCase(decryptByPrivateKey(sign, privateKey));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 组装签名参数
     *
     * @param req  签名参数
     * @param type true：仅用于创建返回签名域  false：创建请求签名域和其他
     * @return String
     * @since 1.0.0
     */
    public static String buildReq(Map<String, String> req, boolean type)
    {
        // 除去数组中的空值和签名参数
        Map<String, String> filteredReq = paraFilter(req, type);
        String prestr = createLinkString(filteredReq);
        return prestr;
    }

    /**
     * 拼接签名字符串
     *
     * @param para Map
     * @return String
     * @since 1.0.0
     */
    public static String createLinkString(Map<String, String> para)
    {

        List<String> keys = new ArrayList<String>(para.keySet());
        // 根据key值作升序排列
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = para.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                sb.append(key).append(QSTRING_EQUAL).append(value);
            } else {
                sb.append(key).append(QSTRING_EQUAL).append(value).append(QSTRING_SPLIT);
            }
        }
        return sb.toString();
    }

    /**
     * 过滤空值、签名域
     * @param para Map
     * @param type true：接口返回签名（使用）  false：请求签名
     * @return Map
     * @since 1.0.0
     */
    public static Map<String, String> paraFilter(Map<String, String> para, boolean type)
    {

        Map<String, String> result = new HashMap<String, String>();

        if (para == null || para.size() <= 0) {
            return result;
        }

        for (String key : para.keySet()) {
            String value = para.get(key);
            if (type && !key.equalsIgnoreCase(RSCODE) && !key.equalsIgnoreCase(RSMEG)) {
                continue;
            }
            // 排除空值、签名域
            if (value == null || value.equals("") || key.equalsIgnoreCase(SIGNATURE)) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    public static void main(String[] args) throws Exception
    {
        Map<String, String> map = generateKeys();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

        //String RSAPublicKey = "<RSAKeyValue><Modulus>AJGHnd9/TQjH1DHqeaFq7x3xqsJOt2i5+Km7kysIGRtJ56A2jXDdTZpceaR7w0/HjOaLFi/hj/A6Uvc49vJZQW2TD4cnuMvLrSd9FltYzv+/VCn+FXVLkQ//jcS7iRxYnaDAFTWUjS9G9M5nhQOaQ2YJkF6GRJSgau1V/xbkW5Ip</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>";

        //String RSAPrivateKey = "<RSAKeyValue><Modulus>AJGHnd9/TQjH1DHqeaFq7x3xqsJOt2i5+Km7kysIGRtJ56A2jXDdTZpceaR7w0/HjOaLFi/hj/A6Uvc49vJZQW2TD4cnuMvLrSd9FltYzv+/VCn+FXVLkQ//jcS7iRxYnaDAFTWUjS9G9M5nhQOaQ2YJkF6GRJSgau1V/xbkW5Ip</Modulus><Exponent>AQAB</Exponent><P>APJI0VwF6F8AD92KYoavK7GWK2zo8DcneNx1ePSKE+wS5jGwkV0I4pLuxbVZbj0N9jGtNcmoeO4LQTE8KPAn6cM=</P><Q>AJnEoahv2oAQUrNiqv8LDKkknbgm2bMGZ3C4P7rYx6LcYtV/FT7sk7MSIAewC+Esp4XRi/xO5fU8Ug0AXB3gqaM=</Q><DP>AOkSXbx9vmUtRRXkmxVfi9PhV1ME3pjgMuc5Zqsv7SxLngAtEBmEg5m/cpgbOO4o8S4mpzigBf1Q/Fzlt6gXbUs=</DP><DQ>UFRd032jmzVrztTj2dsu4QoE7xg0sIbO8R8ABB+No3IYm8oJEldLDciRPRLmEdtfJNSEmempzoSVpL8kYGNmmw==</DQ><InverseQ>AIZ9hweyjwizA5DSYPRHZUxAh98ZNUgvg8f5AcKAP2oaWbsVehcQc43ukxIACjf9ZtSBS67sfsPpe02NHIyh1ac=</InverseQ><D>eg7r28NSohWwJLmLkFY2/b9uwA/zhrPMGtgHRCVDoX6n9dkrYklKyDfdkpZ3JtewlsB7OGeQKSVR5qZxgKxZ0bhDBw7YL4j8p7GHpw1uNRpsBi2fsrE41o1eP1yFTPua7VpXt0VLQ9DHwhITESnl19X1japAV90t5GdfOqAu9h0=</D></RSAKeyValue>";


        String RSAPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCau6ZWN7vOISqTPy4+viH0UyJZ8Ljv7mY1o4MfUfr1Gh7elwwCpoR89o0Xhmwuz+rZ/GTGXmOlDk5u53dIMEzukoNMTqSoISIydA3jFCYK7RPN4jCP+ieJadQKKzSCtbhjVN4mxVykDRa/RNrapMAfgOpzurMfUQ3iX/a0/nyGwwIDAQAB";

        String RSAPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJq7plY3u84hKpM/Lj6+IfRTIlnwuO/uZjWjgx9R+vUaHt6XDAKmhHz2jReGbC7P6tn8ZMZeY6UOTm7nd0gwTO6Sg0xOpKghIjJ0DeMUJgrtE83iMI/6J4lp1AorNIK1uGNU3ibFXKQNFr9E2tqkwB+A6nO6sx9RDeJf9rT+fIbDAgMBAAECgYBOvUsJ+NajMJF/2NjpqMuSj3OdnTgfMIvmo8yn9YnljGNFFkxQVPh6tuRZolgPAlMalquZnpLKfZtAKJ20qI+c0u3qKwCmDm0ALK4GYRJpr1kNOmXreKe2wc1/To/a280Rs4xfP2o7bASmtCThD1QawJ4KG+jqnOhDTijiDD1IAQJBAOkU6uoOanXGN2pyw4xrJRs+qI7Ik0eKHxIjNZmzxsChGDIJsPkjRdRSn+K/4pjNoip+c512lTWr4lVARdUiBgECQQCp8o429BjM2Za2aQFLBVnHEKGy/w4SdzksOfLjhXuGvTHMy4DuCK3t2KC0gSUO3t5jshAYq0GlNZGj2cIh2fTDAkEAkHCTB0q5tJSKYOAPyKPZUeHpiNpJUssIaDADlmVvJI5wNQoGbs3qByLQzC7HWJQOC7FjKlCDNMh4sQXsgKDeAQJAYvEXQQEt6x2G9B4AJbgww4d4TKjXfYKx44C9Wx8cgp5lfvC/2FVvRXkIXu6NL7IrUWmam3IhHtkoOHeP4XtuVwJBAKkjBMnLh65+3U1k0hQdXDBsGwZlm1z3sx7l3DvvsmjR616ZCJcEUbxlDQC85pdON3nBVLfwv1zks4CVAYVC5+c=";


        String str = "clientId:gts||accountNumber:252rw532||source:1||platform:gts||secTimeStamp:" + String.valueOf(System.currentTimeMillis());

        String encryptData = RSAUtil.encryptByPublicKey(str, RSAPublicKey); //进行RSA加密

        String result = decryptByPrivateKey(encryptData, RSAPrivateKey);
        System.out.println(result);

        String[] param = result.split("\\|\\|");
        for (int i = 0; i < param.length; i++) {
            System.out.println(param[i]);
        }
    }

}
