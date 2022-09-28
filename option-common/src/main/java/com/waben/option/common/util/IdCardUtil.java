package com.waben.option.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证信息校验
 * 
 * @author Jesse
 *
 */
public class IdCardUtil {
	
	public static boolean checkChineseName(String value) {
		return value.matches("^[\\u4e00-\\u9fa5]{1}[\\u4e00-\\u9fa5\\.·]{0,8}[\u4e00-\u9fa5]{1}$");
	}

	public static boolean checkChineseName(String value, int length) {
		return value.matches("^[\u4e00-\u9fa5]+{1}") && value.length() <= length;
	}

	public static boolean idCardValidate(String IDStr) {
		try {
			// String errorInfo = "";// 记录错误信息
			String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
			String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
			String Ai = "";
			// ================ 号码的长度 15位或18位 ================
			if (IDStr.length() != 15 && IDStr.length() != 18) {
				// errorInfo = "身份证号码长度应该为15位或18位。";
				return false;
			}
			// =======================(end)========================

			// ================ 数字 除最后以为都为数字 ================
			if (IDStr.length() == 18) {
				Ai = IDStr.substring(0, 17);
			} else if (IDStr.length() == 15) {
				Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
			}
			if (isNumeric(Ai) == false) {
				// errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
				return false;
			}
			// =======================(end)========================

			// ================ 出生年月是否有效 ================
			String strYear = Ai.substring(6, 10);// 年份
			String strMonth = Ai.substring(10, 12);// 月份
			String strDay = Ai.substring(12, 14);// 月份
			if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
				// errorInfo = "身份证生日无效。";
				return false;
			}
			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
						|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
					// errorInfo = "身份证生日不在有效范围。";
					return false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
				// errorInfo = "身份证月份无效";
				return false;
			}
			if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
				// errorInfo = "身份证日期无效";
				return false;
			}
			// =====================(end)=====================

			// ================ 地区码时候有效 ================
			Map<String, String> areaMap = GetAreaCode();
			if (areaMap.get(Ai.substring(0, 2)) == null) {
				// errorInfo = "身份证地区编码错误。";
				return false;
			}
			// ==============================================

			// ================ 判断最后一位的值 ================
			int TotalmulAiWi = 0;
			for (int i = 0; i < 17; i++) {
				TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
			}
			int modValue = TotalmulAiWi % 11;
			String strVerifyCode = ValCodeArr[modValue];
			Ai = Ai + strVerifyCode;

			if (IDStr.contains("x"))
				IDStr.replaceAll("x", "X");
			if (IDStr.length() == 18) {
				if (Ai.equals(IDStr) == false) {
					// errorInfo = "身份证无效，不是合法的身份证号码";
					return false;
				}
			}
			// =====================(end)=====================
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 *
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
        return m.matches();
	}

	/**
	 * 根据身份证号计算年龄
	 */
	public static Integer getPersonAgeFromIdCard(String idcard) {
		// 截取身份证中出行人出生日期中的年、月、日
		Integer personYear = Integer.parseInt(idcard.substring(6, 10));
		Integer personMonth = Integer.parseInt(idcard.substring(10, 12));
		Integer personDay = Integer.parseInt(idcard.substring(12, 14));

		Calendar cal = Calendar.getInstance();
		// 得到当前时间的年、月、日
		Integer yearNow = cal.get(Calendar.YEAR);
		Integer monthNow = cal.get(Calendar.MONTH) + 1;
		Integer dayNow = cal.get(Calendar.DATE);

		// 用当前年月日减去生日年月日
		Integer yearMinus = yearNow - personYear;
		Integer monthMinus = monthNow - personMonth;
		Integer dayMinus = dayNow - personDay;

		Integer age = yearMinus; // 先大致赋值

		if (yearMinus == 0) { // 出生年份为当前年份
			age = 0;
		} else { // 出生年份大于当前年份
			if (monthMinus < 0) {// 出生月份小于当前月份时，还没满周岁
				age = age - 1;
			}
			if (monthMinus == 0) {// 当前月份为出生月份时，判断日期
				if (dayMinus < 0) {// 出生日期小于当前月份时，没满周岁
					age = age - 1;
				}
			}
		}
		return age;
	}

	/**
	 * 功能：判断字符串是否为数字
	 *
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
        return isNum.matches();
	}

	private static Map<String, String> GetAreaCode() {
		Map<String, String> map = new HashMap<>();
		map.put("11", "北京");
		map.put("12", "天津");
		map.put("13", "河北");
		map.put("14", "山西");
		map.put("15", "内蒙古");
		map.put("21", "辽宁");
		map.put("22", "吉林");
		map.put("23", "黑龙江");
		map.put("31", "上海");
		map.put("32", "江苏");
		map.put("33", "浙江");
		map.put("34", "安徽");
		map.put("35", "福建");
		map.put("36", "江西");
		map.put("37", "山东");
		map.put("41", "河南");
		map.put("42", "湖北");
		map.put("43", "湖南");
		map.put("44", "广东");
		map.put("45", "广西");
		map.put("46", "海南");
		map.put("50", "重庆");
		map.put("51", "四川");
		map.put("52", "贵州");
		map.put("53", "云南");
		map.put("54", "西藏");
		map.put("61", "陕西");
		map.put("62", "甘肃");
		map.put("63", "青海");
		map.put("64", "宁夏");
		map.put("65", "新疆");
		map.put("71", "台湾");
		map.put("81", "香港");
		map.put("82", "澳门");
		map.put("91", "国外");
		return map;
	}

}
