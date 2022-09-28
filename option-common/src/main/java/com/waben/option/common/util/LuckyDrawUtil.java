package com.waben.option.common.util;

import java.math.BigDecimal;

public class LuckyDrawUtil {

    public static Integer getRand(float obj[]) {
        Integer result = null;
        try {
            float sum = 0.0f;
            float min = 0.0f;
            for (int i = 0; i < obj.length; i++) {
                BigDecimal beforeSum = new BigDecimal(Float.toString(sum));
                BigDecimal objValue = new BigDecimal(Float.toString(obj[i]));
                sum = beforeSum.add(objValue).floatValue();
            }
            for (int i = 0; i < obj.length; i++) {
                BigDecimal db = new BigDecimal(Math.random() * (sum - min) + min);
                BigDecimal b = new BigDecimal(Float.toString(obj[i]));
                if (compareMethod(db, b) == -1) {
                    result = i;
                    break;
                } else {
                    sum -= obj[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int compareMethod(BigDecimal a, BigDecimal b) {
        return a.compareTo(b);
    }

    public static String award(Object[][] prizeArr) {
        float obj[] = new float[prizeArr.length];
        for (int i = 0; i < prizeArr.length; i++) {
            obj[i] = Float.parseFloat(prizeArr[i][2].toString());
        }
        Integer prizeId = getRand(obj);
        String msg = (String) prizeArr[prizeId][1];
        return msg;
    }

    public static Object[][] prizeArr = new Object[][]{
            {1, "500", 0.5},
            {2, "1000", 0.5},
            {3, "2000", 0},
            {4, "3000", 0},
            {5, "10000", 0},
            {6, "100000", 0},
            {7, "200000", 0},
            {8, "500000", 0}
    };
}
