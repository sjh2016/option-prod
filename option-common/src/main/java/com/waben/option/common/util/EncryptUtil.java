package com.waben.option.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {

    public static String MD5(String message) {
        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(message.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * md5 加密
     *
     * @param message
     * @return
     */
    public static String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] messageByte = message.getBytes(StandardCharsets.UTF_8);
            byte[] md5Byte = messageDigest.digest(messageByte);
            md5 = bytesToHex(md5Byte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String getFileMD5(File file) {
        String MD5 = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            MD5 = bytesToHex(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MD5;
    }

    /**
     * SHA256 加密
     *
     * @param message
     * @return
     */
    public static String getSHA256(String message) {
        String sha256 = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] messageByte = message.getBytes(StandardCharsets.UTF_8);
            byte[] sha256Byte = messageDigest.digest(messageByte);
            sha256 = bytesToHex(sha256Byte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha256;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if (num < 0) {
                num += 256;
            }
            if (num < 16) {
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

}  