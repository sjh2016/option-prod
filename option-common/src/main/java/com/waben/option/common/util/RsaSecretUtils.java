package com.waben.option.common.util;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

public class RsaSecretUtils {
    private static final String RSA = "RSA";

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * 生成秘钥对
     *
     * @return
     * @throws Exception
     */
    public KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取公钥(Base64编码)
     *
     * @param keyPair 秘钥对
     * @return
     */
    public String getPublicKeyBase64(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 获取私钥(Base64编码)
     *
     * @param keyPair 秘钥对
     * @return
     */
    public String getPrivateKeyBase64(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 将Base64编码后的公钥转换成PublicKey对象
     *
     * @param publicKeyBase64 公钥base64
     * @return
     */
    public static PublicKey getPublicKey(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64Utils.decode(publicKeyBase64.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     *
     * @param privateKeyBase64 私钥base64
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64Utils.decode((privateKeyBase64.getBytes()));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密
     *
     * @param plaintext       明文
     * @param publicKeyBase64 公钥base64
     * @return 密文数组base64编码后的字符串
     */
    public static String publicKeyEncrypt(String plaintext, String publicKeyBase64) {
        try {
            // 获取明文字节数组
            byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance(RSA);
            // 编码前设定编码方式及密钥
            PublicKey publicKey = getPublicKey(publicKeyBase64);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int keyBit = getKeySize(publicKey);
            int inputLen = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            int step = keyBit / 8 - 11;

            for (int i = 0; inputLen - offSet > 0; offSet = i * step) {
                byte[] cache;
                if (inputLen - offSet > step) {
                    cache = cipher.doFinal(bytes, offSet, step);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                ++i;
            }
            // 密文字节数组
            byte[] ciphertextBytes = out.toByteArray();
            out.close();
            // 返回密文字节数组base64编码后的字符串
            return Base64Utils.encodeToString(ciphertextBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥解密
     *
     * @param ciphertext      密文
     * @param publicKeyBase64 公钥base64
     * @return 明文
     */
    public static String publicKeyDecrypt(String ciphertext, String publicKeyBase64) {
        try {
            // 密文base64解码字节数组
            byte[] bytes = Base64Utils.decode(ciphertext.getBytes());
            Cipher cipher = Cipher.getInstance(RSA);
            PublicKey publicKey = getPublicKey(publicKeyBase64);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            int keyBit = getKeySize(publicKey);
            int inputLen = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            int step = keyBit / 8;

            for (int i = 0; inputLen - offSet > 0; offSet = i * step) {
                byte[] cache;
                if (inputLen - offSet > step) {
                    cache = cipher.doFinal(bytes, offSet, step);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                ++i;
            }
            // 明文字节数组
            byte[] plaintextBytes = out.toByteArray();
            out.close();
            return new String(plaintextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 私钥加密
     *
     * @param plaintext        明文
     * @param privateKeyBase64 私钥base64
     * @return
     */
    public static String privateKeyEncrypt(String plaintext, String privateKeyBase64) {
        try {
            // 获取明文字节数组
            byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance(RSA);
            // 编码前设定编码方式及密钥
            PrivateKey privateKey = getPrivateKey(privateKeyBase64);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            int keyBit = getKeySize(privateKey);
            int inputLen = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            int step = keyBit / 8 - 11;

            for (int i = 0; inputLen - offSet > 0; offSet = i * step) {
                byte[] cache;
                if (inputLen - offSet > step) {
                    cache = cipher.doFinal(bytes, offSet, step);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                ++i;
            }
            // 密文字节数组
            byte[] ciphertextBytes = out.toByteArray();
            out.close();
            // 返回密文字节数组base64编码后的字符串
            return Base64Utils.encodeToString(ciphertextBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 私钥解密
     *
     * @param ciphertext       密文
     * @param privateKeyBase64 私钥base64
     * @return 明文
     */
    public static String privateKeyDecrypt(String ciphertext, String privateKeyBase64) {
        try {
            // 密文base64解码字节数组
            byte[] bytes = Base64Utils.decode(ciphertext.getBytes());
            Cipher cipher = Cipher.getInstance(RSA);
            PrivateKey privateKey = getPrivateKey(privateKeyBase64);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int keyBit = getKeySize(privateKey);
            int inputLen = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            int step = keyBit / 8;

            for (int i = 0; inputLen - offSet > 0; offSet = i * step) {
                byte[] cache;
                if (inputLen - offSet > step) {
                    cache = cipher.doFinal(bytes, offSet, step);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                ++i;
            }
            // 明文字节数组
            byte[] plaintextBytes = out.toByteArray();
            out.close();
            return new String(plaintextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 私钥加签
     *
     * @param ciphertext       密文
     * @param privateKeyBase64 私钥Base64
     * @return 加密后的base64签名
     */
    public static String rsaSign(String ciphertext, String privateKeyBase64) {
        try {
            // 密文字节数组
            byte[] ciphertextBytes = Base64Utils.decode(ciphertext.getBytes());
            PrivateKey privateKey = getPrivateKey(privateKeyBase64);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(ciphertextBytes);
            byte[] signed = signature.sign();
            return Base64Utils.encodeToString(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥验签
     *
     * @param ciphertext      密文
     * @param sign            签名
     * @param publicKeyBase64 公钥base64
     * @return 是否篡改了数据
     */
    public static boolean rsaSignCheck(String ciphertext, String sign, String publicKeyBase64) {
        try {
            // 密文base64解码字节数组
            byte[] ciphertextBytes = Base64Utils.decode(ciphertext.getBytes());
            // 签名base64解码字节数组
            byte[] signBytes = Base64Utils.decode(sign.getBytes());
            PublicKey publicKey = getPublicKey(publicKeyBase64);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(publicKey);
            signature.update(ciphertextBytes);
            return signature.verify(signBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取公钥长度
     *
     * @param publicKey 公钥
     * @return
     */
    public static int getKeySize(PublicKey publicKey) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        return rsaPublicKey.getModulus().bitLength();
    }

    /**
     * 获取私钥长度
     *
     * @param privateKey 私钥
     * @return
     */
    public static int getKeySize(PrivateKey privateKey) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        return rsaPrivateKey.getModulus().bitLength();
    }

    public static void main(String[] args) throws Exception {
        // 获取密钥
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        String privateKey = Base64Utils.encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
        System.out.println("1.私钥:" + privateKey);
        System.out.println("2.公钥:" + publicKey);

        String text = "hello world";
        System.out.println("3.明文:" + text);
        // 使用公钥加密
        String encryptText = publicKeyEncrypt(text, publicKey);
        System.out.println("4.密文:" + encryptText);
        String changeText = "goodbye world";
        System.out.println("5.篡改后明文:" + changeText);
        // 使用公钥加密
        String encryptChangeText = publicKeyEncrypt(changeText, publicKey);
        System.out.println("6.篡改后密文:" + encryptChangeText);

        // 解密密文
        String decryptText = privateKeyDecrypt(encryptText, privateKey);
        // 解密篡改后密文
        String decryptChangeText = privateKeyDecrypt(encryptChangeText, privateKey);
        // 解密后明文
        System.out.println("7.解密后明文:" + decryptText);
        System.out.println("7.解密后篡改明文:" + decryptChangeText);
        // 加签
        String sign = rsaSign(encryptText, privateKey);
        System.out.println("8.签名:" + sign);
        // 验签
        boolean pass = rsaSignCheck(encryptText, sign, publicKey);
        System.out.println("9.是否一致:" + pass);

    }


    public static String mapToQueryString(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!org.springframework.util.StringUtils.isEmpty(entry.getValue())) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        System.out.println("builder:"+builder.toString());
        return builder.toString();
    }

}
