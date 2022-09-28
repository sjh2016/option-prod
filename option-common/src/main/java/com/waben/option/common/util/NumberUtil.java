package com.waben.option.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class NumberUtil {

    public static void main(String[] args) {
    	for(int i = 0; i < 177;i++) {
    		String code = generateCode(8);
    		System.out.println(code.substring(0, 2) + "****" + code.substring(6));
		}
    }

    private static Random random = new Random();

    /**
     * 生成数位代码字符串
     *
     * @param num
     * @return
     */
    public static String generateCode(int num) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < num; i++) {
            buf.append(random.nextInt(10));
        }
        return buf.toString();
    }

    /**
     * 计算小数位精度
     *
     * @param tickSize
     * @return
     */
    public static int getScale(BigDecimal value) {
        String s = value.stripTrailingZeros().toPlainString();
        if (s.indexOf(".") >= 0) {
            return s.substring(s.indexOf(".") + 1).length();
        } else {
            return 0;
        }
    }

    /**
     * 计算合约均价
     *
     * @param totalPrice 总价
     * @param volume     总量
     * @param tickSize   最小波动价位
     * @return
     */
    public static BigDecimal getContractAveragePrice(BigDecimal totalPrice, BigDecimal volume, int pricePrecision) {
        BigDecimal averagePrice = totalPrice.divide(volume, pricePrecision, RoundingMode.DOWN);
        return averagePrice;
    }

    public static String getFourRandom() {
        Random random = new Random();
        String fourRandom = random.nextInt(100000) + "";
        int randLength = fourRandom.length();
        if (randLength < 4) {
            for (int i = 1; i <= 4 - randLength; i++)
                fourRandom = "0" + fourRandom;
        }
        return fourRandom;
    }

    public static int random() {
        return (int) Math.random() * 900 + 100;
    }

    public static String randomUpperCase() {
        char c = (char) (int) (Math.random() * 26 + 65);
        return String.valueOf(c);
    }
}
