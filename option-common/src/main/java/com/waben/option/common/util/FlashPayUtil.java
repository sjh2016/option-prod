package com.waben.option.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class FlashPayUtil {

    //请求参数解密
    public static boolean decryptRequest(Map<String, String> data, String publicKey) {
        log.info("解密明文content=[{}]", JSON.toJSONString(data));
        try {
            boolean verify = verifySign(data, publicKey);  // 签名验证
            log.info("支付公钥验签结果[{}]", verify);
            return verify;
        } catch (Exception e) {
            log.info("解密失败Exception: ", e);
        }
        return false;
    }

    /**
     * 验证签名
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean verifySign(Map<String, String> maps, String publickey) {
        String platSign = maps.get("sign"); // 签名
        System.out.println("platSign:" + platSign);
        List<String> paramNameList = new ArrayList<String>();
        for (String key : maps.keySet()) {
            if (!"sign".equals(key)) {
                paramNameList.add(key);
            }
        }
        Collections.sort(paramNameList);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < paramNameList.size(); i++) {
            String name = paramNameList.get(i);
            stringBuilder.append(String.valueOf(maps.get(name)));
        }
        log.info("keys:" + stringBuilder);
        String decryptSign = "";
        try {
            decryptSign = publicDecrypt(platSign, getPublicKey(publickey));
        } catch (Exception e) {
            log.error("", e);
        }
        log.info("decryptSign:" + decryptSign);
        if (!stringBuilder.toString().equalsIgnoreCase(decryptSign)) {
            return false;
        }
        return true;
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec1(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    private static byte[] rsaSplitCodec1(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        return resultDatas;
    }


    //加密请求参数
    public static String encryptRequest(Map<String, Object> maps, String privateKey) {
        log.info("加密明文[{}]", JSON.toJSONString(maps));
        String signStr = null;
        try {
            List<String> paramNameList = new ArrayList<String>();
            for (String key : maps.keySet()) {
                if (!"sign".equals(key)) {
                    paramNameList.add(key);
                }
            }
            Collections.sort(paramNameList);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < paramNameList.size(); i++) {
                String name = paramNameList.get(i);
                stringBuilder.append(maps.get(name));
            }
            log.info("加密排序明文[{}]", stringBuilder.toString());
            signStr = privateEncrypt(stringBuilder.toString(), getPrivateKey(privateKey));  // 私钥加密
        } catch (Exception e) {
            log.info("加密失败Exception[{}], {}", signStr, e);
            signStr = null;
        }
        log.info("加密密文[{}]", signStr);
        return signStr;
    }

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes("UTF-8"), privateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        return resultDatas;
    }

}
