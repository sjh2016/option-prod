package com.waben.option.common.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DecimalUtil {
	
	public static void main(String[] args) {
		Map<BigDecimal, BigDecimal[]> tempMap = new HashMap<>();
		System.out.println(tempMap.values().size());
	}
	
	public static BigDecimal getMinPrecisionValue(int pricePrecision) {
		BigDecimal result = BigDecimal.ONE;
		for (int i = 0; i < pricePrecision; i++) {
			result = result.divide(new BigDecimal(10));
		}
		return result;
	}

}
