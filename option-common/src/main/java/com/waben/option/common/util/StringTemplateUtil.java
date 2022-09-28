package com.waben.option.common.util;

import java.util.Map;
import java.util.Set;

public class StringTemplateUtil {

	/**
	 * 模板格式化
	 * 
	 * <pre>
	 * example
	 * value hello {name} world!
	 * params = {"name": "zhangsan"} 
	 * result = value hello zhangsan world!
	 * </pre>
	 */
	public static String format(String value, Map<String, String> params) {
		StringBuilder builder = new StringBuilder(value);
		if (params != null && params.size() > 0) {
			Set<String> keys = params.keySet();
			for (String key : keys) {
				String seat = "{" + key + "}";
				replaceAll(builder, seat, params.get(key));
			}
		}
		return builder.toString();
	}

	public static void replaceAll(StringBuilder builder, String from, String to) {
		int index = builder.indexOf(from);
		while (index != -1) {
			builder.replace(index, index + from.length(), to);
			index += to.length();
			index = builder.indexOf(from, index);
		}
	}

	public static String deleteString2(String str, char delChar) {
		StringBuffer stringBuffer = new StringBuffer("");
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != delChar) {
				stringBuffer.append(str.charAt(i));
			}
		}
		return stringBuffer.toString();
	}

}
