package com.waben.option.common.component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.waben.option.common.constants.Constants;

public class LocaleContext {

	private static final ThreadLocal<String> resource = new ThreadLocal<>();

	private static final Map<String, String> localeMap = new HashMap<>();

	public static void set(String locale) {
		resource.set(locale);
	}

	public static void put(String language, String country) {
		localeMap.put(language, country);
	}

	public static String get() {
		String locale = resource.get();
		if (locale == null) {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (servletRequestAttributes != null) {
				HttpServletRequest request = servletRequestAttributes.getRequest();
				locale = request.getHeader("locale");
			}
		}
		if (locale == null)
			locale = Constants.DEFAULT_LANGUAGE;
		return locale;
	}

	public static String getLocale() {
		String result = get().split("_")[0];
		return result;
//		if (result != null && ("zh".equals(result.trim()) || "id".equals(result.trim()))) {
//			return result.trim();
//		} else {
//			return "id";
//		}
	}

	public static String getCountryCode() {
		String result = null;
		if (!get().contains("_")) {
			result = localeMap.get(get());
		}
		String fullLocale = get();
		if (fullLocale != null && fullLocale.split("_").length >= 2) {
			result = fullLocale.split("_")[1];
		}
		return result;
//		if (result != null && ("CN".equals(result.trim()) || "ID".equals(result.trim()))) {
//			return result.trim();
//		} else {
//			return "ID";
//		}
	}

	public static void remove() {
		resource.remove();
	}

	public static void main(String[] args) {
		Locale locale = new Locale("zh");
		ResourceBundle rb = ResourceBundle.getBundle("message", locale);
		System.out.println(rb.getLocale().getCountry());
	}

}
