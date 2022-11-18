package com.waben.option.thirdparty.service.pay.we;

import com.alibaba.fastjson.JSON;
import com.waben.option.common.util.RsaUtil;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

public class TestDemo {

    public static String mapToQueryString(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!org.springframework.util.StringUtils.isEmpty(entry.getValue())) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        System.out.println("builder:"+builder.toString());
        return builder.toString();
    }

    public static void main(String [] args) throws Exception {
            BigDecimal big = new BigDecimal(2800);
            BigDecimal bigs = new BigDecimal(74);
        System.out.println(big.divide(bigs,2,BigDecimal.ROUND_HALF_UP));
    }

    public static  String signRsA(String param) throws Exception {
        Map<String, String> retMap = RsaUtil.genKeyPair();
        String priKey = retMap.get("priKey");
        System.out.println("priKey:"+priKey);
        String priCipherText = RsaUtil.encryptByPrivate(param, priKey);
        priCipherText = URLEncoder.encode(priCipherText,"UTF-8");
        return priCipherText;
    }

}
