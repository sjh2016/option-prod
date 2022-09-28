package com.waben.option.common.component;

import java.util.Map;

public class RequestParamContext {

	private static final ThreadLocal<Map<String, Object>> resource = new ThreadLocal<>();
	
	public static void set(Map<String, Object> map) {
		resource.set(map);
	}
	
	public static Object getParamter(String key) {
		Map<String, Object> map = resource.get();
		if(map != null) {
			return resource.get().get(key);
		} else {
			return null;
		}
	}

	public static void remove() {
		resource.remove();
	}
	
}
