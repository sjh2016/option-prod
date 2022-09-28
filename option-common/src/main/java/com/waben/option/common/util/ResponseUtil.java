package com.waben.option.common.util;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.Response;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static Map<String, Object> buildData(String cmd, Response<?> response, int requestId) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", response.getCode());
        map.put("msg", response.getMsg());
        map.put("cmd", cmd);
        map.put("requestId", requestId);
        if (response != null) {
            map.put("data", response.getData());
        }
        return map;
    }

    public static Map<String, Object> buildData(String cmd, ServerException exception, int requestId) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", exception.getCode());
        map.put("msg", exception.getMsg());
        map.put("cmd", cmd);
        map.put("requestId", requestId);
        return map;
    }

    public static Map<String, Object> buildData(String cmd, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("cmd", cmd);
        map.put("data", data);
        return map;
    }

}
