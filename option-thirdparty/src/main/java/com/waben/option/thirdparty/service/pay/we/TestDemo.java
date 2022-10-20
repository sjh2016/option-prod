package com.waben.option.thirdparty.service.pay.we;

import com.alibaba.fastjson.JSON;
import com.waben.option.common.util.RsaUtil;
import okhttp3.*;

import java.io.IOException;
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
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        System.out.println(startDateTime);
        System.out.println(endDateTime);
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
