package com.waben.option.common.util;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RsaUtil {

    private static final Logger log = LoggerFactory.getLogger(RsaUtil.class);

    //加密算法RSA
    private static final String KEY_ALGORITHM = "RSA";
    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final String CHARSET = "UTF-8";

    public static Map<String,String> genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024);
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        Map<String,String> retMap = new HashMap<>();
        retMap.put("pubKey",publicKeyString);
        retMap.put("priKey",privateKeyString);
        return retMap;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     *
     * @param signSrc  源
     * @param cipherText  待解密数据
     * @param publickey   公钥
     * @return
     * @throws Exception
     */
    public static boolean verifySignByPub(String signSrc,String cipherText,String publickey){
        Boolean verifySign = false;
        try {
            String decryptSign = decryptByPublic(cipherText, getPublicKey(publickey));
            if (signSrc.equalsIgnoreCase(decryptSign)) {
                verifySign = true;
            }
        } catch (Exception e){
            log.error("公钥解密时出现异常:{}",e.getMessage());
        }
        return verifySign;
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */
    public static String encryptByPrivate(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength());
            return Base64.encodeBase64URLSafeString(bytes);
        }catch(Exception e){
            throw new RuntimeException("私钥加密字符串[" + data + "]时异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data 源
     * @param privateKey 私钥
     * @return
     */
    public static String encryptByPrivate(String data, String privateKey){
        try{
            //获取私钥
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), rsaPrivateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("私钥加密字符串[" + data + "]时异常", e);
        }
    }


    /**
     * 公钥解密
     * @param data 源
     * @param publicKey 公钥
     * @return
     */
    public static String decryptByPublic(String data, String publicKey){
        try{
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), rsaPublicKey.getModulus().bitLength()), "UTF-8");
        }catch(Exception e){
            throw new RuntimeException("公钥解密字符串[" + data + "]时异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */
    public static String decryptByPublic(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), "UTF-8");
        }catch(Exception e){
            throw new RuntimeException("公钥解密字符串[" + data + "]时异常", e);
        }
    }

    /**
     * 数据分段加密
     * @param cipher
     * @param opmode
     * @param datas
     * @param keySize
     * @return
     * @throws Exception
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize)throws Exception{
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        byte[] resultDatas = null;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            resultDatas = out.toByteArray();
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }finally {
            out.close();
        }
        return resultDatas;
    }


    public static void main(String[] args)throws Exception {
        Map<String, String> retMap = RsaUtil.genKeyPair();
        String pubKey = retMap.get("pubKey");
        String priKey = retMap.get("priKey");

//        System.out.println("生成公钥\n"+pubKey);
//        System.out.println("公钥长度\n"+pubKey.length());
//
//        try {
//            RSAPublicKey publicKey = RsaUtil.getPublicKey(pubKey);
//            if(publicKey != null){
//                System.out.println("校验公钥合法性\n"+true);
//            }
//        }catch (Exception e){
//            System.out.println("校验公钥合法性\n"+false);
//        }
//
//        System.out.println("生成私钥\n"+priKey);
//        System.out.println("私钥长度\n"+priKey.length());
//
//
//        String message = "bankCode=TPB&busi_code=100303&ccy_no=INR&countryCode=IND&goods=goods&mer_no=gm761100000033104&mer_order_no=20210907123407abc&notifyUrl=https://google.com&order_amount=100.00&pemail=test@gmail.com&phone=123&pname=zhangsan";
//        System.out.println("明文\n"+message);
//        System.out.println("明文长度\n"+message.length());
//
//        String priCipherText = RsaUtil.encryptByPrivate(message, priKey);
//        System.out.println("私钥加密后密文\n"+priCipherText);
//
//        priCipherText = URLEncoder.encode(priCipherText,"UTF-8");
//        System.out.println("URL编码后"+priCipherText+"\n");
//
//        priCipherText = URLDecoder.decode(priCipherText, "UTF-8");
//        System.out.println("URL解码后"+priCipherText+"\n");
//
//        String priPlainText = RsaUtil.decryptByPublic(priCipherText, pubKey);
//        System.out.println("公钥解密后明文\n"+priPlainText);
//
//        boolean verifySign = RsaUtil.verifySignByPub(message, priCipherText, pubKey);
//        System.out.println("验签结果\n"+verifySign);



        Map<String, Object> params = new TreeMap<>();
        params.put("mer_no","861100000066143");
        params.put("mer_order_no", "1578284302850850816");
        params.put("pname", "zhangsa");
        params.put("pemail", "364063862@qq.com");
        params.put("phone","2328382832");
        params.put("order_amount", "200");
        params.put("ccy_no","INR");
        params.put("busi_code","100303");
        String notifyUrlE =      URLEncoder.encode("http://13.245.161.1:9700/payment_callback/wepay/payCallback/","UTF-8");
        params.put("notifyUrl", "http://13.245.161.1:9700/payment_callback/wepay/payCallback/");
        String pageUrlE = URLEncoder.encode("https://m.a-gold.pro/","UTF-8");
        params.put("pageUrl","https://m.a-gold.pro/");
        //params.put("goods","test");
        // 签名
        priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMJmMnRh425lD2O+WBisHDNLhxcuB1YCsU+Gbso97IIy39Tb2le2uTWquuJsaPaTyjg3mSIUqvdFtjHfEGA6IA9hJQgoHd+ERk3ZJ5RZYrf+8TENMOqjB6YsEXkqyxiNyg+T5xTq+n3TfiogO5UhGi5xOy4jihPNhbB0qZESoi6BAgMBAAECgYEApPue7m71Wgei2//7PFU1ScNbzyxdRX3bhaaN+E49nvIWTMyZrb+ByC4xlWB0ZHBhOvzUlbv8rQcY7ZJmj4RFF8xt2z/0w/qfGC8XdLy1F+F8jT+dreFeroPW4hBwEohvFSeUD20jt6hbszRxw+WEKVWX9Sbj5YhnQPCqUETU4FECQQDx9MrBvpHvdV8qBvtYy0x1BnIHV20pYlbglROXykFUlYoOaCydCNGSfdWOdomSsmqduQp0qu6bETvgPTXXeCm7AkEAza7CXyMG4yOoKUia0v+Q/hYNyGZX1G0Q1ZicjTGwWzPCaWCI0kPo185Kz6S26E3ihu67K9HjP7BD8HFu8+2W8wJASWFYbNIDKICMUA62BAQEOCmwQ5G8RaTRfGM6AfsRPTntGNl3TljyVl9beU1yA8+gjh0kkqWUn+rmIefugF6tKQJAZCmluETLdtOHcctMslIInuPIhH3qaaVPXGU8X7oB4vgaxcxf9N1jR6zLW8Ef3ZMILKOXfKhynTJOc4oZ+SoLpwJAC11W6c2+gxnW96fGO99+tEXeZtODNzeGHQ8mWxx77anAuQuKo8fnlvBWIcfhXtjUS4wjD6BtdSShswqd3WO99A==";
        String sign = RsaUtil.encryptByPrivate(mapToQueryString(params), priKey);
        sign = URLEncoder.encode(sign,"UTF-8");
        System.out.println("sign:"+sign);
        //String sign = EncryptUtil.getMD5(mapToQueryString(params)).toLowerCase();
        //params.put("sign_type", "RSA");
        params.put("sign", sign);

        //sign ="hv_OoRZejna_3baya6wSRadYNvV718BQ_QLthR56OWkLx-4FrOKCawwM_4y33rcjpLM8bwqr_mRYGMQ19PKKmBB5HABQXhBBZ76x2Dqyl77kRSOHlt3akG8mKGiq8p8etP-jS8va2eHpLS96lTt-Kc5wcgrlFjFwZ1s8xlLa0NQOIlED1afGnLEsZRXyf0oyZY-NpOUDJYHGus6aTfbfR5gd6Z-yQ8h4ELK8D_UNJTUQBVkxL4ffXI2KR2fGoWyaOjX6ZCXwgA9yg5Ykp5whwtzckE0fQzZeFJPXKn_ad_tTGcdx5MGnKcm8I_vT39a8HjfQcT3VWskGDsNr5Tt8VA";
//        String sing = RsaUtil.decryptByPublic(sign, pubKey);
//        System.out.println("公钥解密后明文\n"+sing);
        String jsonStr = JSON.toJSONString(params);
        System.out.println("jsonStr:"+jsonStr);
        String orderUrl = "https://crsqi.mywsypay.com/ty/orderPay";
        Request postRequest = new Request.Builder().url(orderUrl).post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr)).build();
        Response response = new OkHttpClient().newCall(postRequest).execute();
        String json = response.body().string();
        System.out.println(json);

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
