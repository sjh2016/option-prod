package com.waben.option.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PatternUtil {

	private final static Map<String, String> REGULAR_PATTERN_MAP = new HashMap<String, String>();

	static {
		REGULAR_PATTERN_MAP.put("telephone", "^(0\\d{2,3}[-| ]?)?(\\d{7,8})([-| ]?\\d{3,5})?$");
//		REGULAR_PATTERN_MAP.put("mobile",
//				"^0?(13[0-9]|15[012356789]|18[0123456789]|14[57]|17[0123456789]|14[57]|166|19[89])[0-9]{8}$");
		REGULAR_PATTERN_MAP.put("mobile", "^\\d+$");
		REGULAR_PATTERN_MAP.put("email", "^([a-zA-Z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");
		REGULAR_PATTERN_MAP.put("nickname",
				"^(?=.{1,8}$)(?![_-])(?!.*[_-]{2})[\\u4e00-\\u9fa5_a-zA-Z0-9_-]+(?<![_-])$");
		REGULAR_PATTERN_MAP.put("password",
				"^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,20}$");
		REGULAR_PATTERN_MAP.put("image_url", "(https?:\\/\\/.*\\.(?:png|jpg|gif))");
		REGULAR_PATTERN_MAP.put("qq", "^[1-9][0-9]{4,10}$");
		REGULAR_PATTERN_MAP.put("channel_name", "^(?=.{1,30}$)(?![_-])(?!.*[_-]{2})[a-zA-Z0-9_-]+(?<![_-])$");
	}

	private static boolean isPatternFromMap(String key, String str) {
		String pattern = REGULAR_PATTERN_MAP.get(key);
		return isPattern(pattern, str);
	}

	public static boolean isPattern(String patern, String str) {
		if (StringUtils.isNotEmpty(str)) {
			Pattern pattern = Pattern.compile(patern);
			return pattern.matcher(str).matches();
		}
		return false;
	}

	public static boolean isTelephone(String str) {
		return isPatternFromMap("telephone", str);
	}
	
	public static boolean isMobile(String str) {
		return isPatternFromMap("mobile", str);
	}

	public static boolean isEmail(String str) {
		return isPatternFromMap("email", str);
	}

	public static boolean isNickname(String str) {
		return isPatternFromMap("nickname", str);
	}

	public static boolean isPassword(String str) {
		return isPatternFromMap("password", str);
	}

	public static boolean isImageUrl(String str) {
		return isPatternFromMap("image_url", str);
	}

	public static boolean isQQ(String str) {
		return isPatternFromMap("qq", str);
	}
	
	public static boolean isChannelName(String str) {
		return isPatternFromMap("channel_name", str);
	}

	
}
