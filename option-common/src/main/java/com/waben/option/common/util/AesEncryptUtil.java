package com.waben.option.common.util;

import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * <pre>
 * js实现加密
 * 
 * https://blog.csdn.net/qq_39315434/article/details/106069894
 * 
 * step1: npm install crypto-js --save-dev
 * 
 * 
 * </pre>
 * 
 */
public class AesEncryptUtil {

	public static void main(String[] args) {
		String content = "{\"username\":\"13999999999\",\"password\":\"123456\"}";
		System.out.println(encrypt(content));

		String enData = "XgCZJH90xJI+NLcmIbEBnl9f2XYPa7O3DjeFfJ676IJ6lSFKXGkHaRhq21FtwI1b";
		System.out.println(decrypt(enData));
	}

	public static boolean initialized = false;

	public static final String ALGORITHM = "AES/ECB/PKCS7Padding";

	public static final String KEY = "89d1361d73123ff9";

	public static String encrypt(String content) {
		String result = new String(Base64.getEncoder().encode(Aes256Encode(content, KEY.getBytes())));
//		result = result.replaceAll("/", "_");
//		result = result.replaceAll("\\+", "-");
		return result;
	}

	public static String decrypt(String content) {
		String result = Aes256Decode(Base64.getDecoder().decode(content), KEY.getBytes());
		return result;
	}

	/**
	 * @param String str 要被加密的字符串
	 * @param byte[] key 加/解密要用的长度为32的字节数组（256位）密钥
	 * @return byte[] 加密后的字节数组
	 */
	public static byte[] Aes256Encode(String str, byte[] key) {
		initialize();
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES"); // 生成加密解密需要的Key
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			result = cipher.doFinal(str.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param byte[] bytes 要被解密的字节数组
	 * @param byte[] key 加/解密要用的长度为32的字节数组（256位）密钥
	 * @return String 解密后的字符串
	 */
	public static String Aes256Decode(byte[] bytes, byte[] key) {
		initialize();
		String result = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES"); // 生成加密解密需要的Key
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decoded = cipher.doFinal(bytes);
			result = new String(decoded, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void initialize() {
		if (initialized)
			return;
		Security.addProvider(new BouncyCastleProvider());
		initialized = true;
	}

}
