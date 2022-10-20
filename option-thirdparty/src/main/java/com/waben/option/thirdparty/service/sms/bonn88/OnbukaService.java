package com.waben.option.thirdparty.service.sms.bonn88;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RefreshScope
@Service("onbukaService")
public class OnbukaService extends AbstractBaseSmsService {

    @Value("${sms.onbuka.appkey:jfPNxLVW}")
    private String appkey;

    @Value("${sms.onbuka.appsecret:7k8Qz6V5}")
    private String appsecret;

    @Value("${sms.onbuka.appid:vTEoVM19}")
    private String appid;

    @Value("${sms.onbuka.url:https://api.onbuka.com/v3/sendSms}")
    private String url;



//    @Resource
//    private RedisTemplate<Serializable, Object> redisTemplate;

    @Resource
    private OkHttpClient okHttpClient;

    @Override
    public boolean sendCode(String areaCode, String phone, String code, String content, String ip) {
        log.info("xxxxxxxxxx->>>>>{},{}",appid,appkey);
        verifyRequestCount(ip);
        boolean flag = true;
        try {
            if (phone.startsWith("0")) {
                phone = phone.substring(1);
            }
            final String datetime = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
            String k = appkey+appsecret+datetime;
            String sign = EncryptUtil.getMD5(k).toLowerCase();


            Map<String,String> map = new HashMap<String,String>();
            map.put("appId",appid);
            map.put("numbers","234"+phone);
            map.put("content","Your capcha is "+code);
            map.put("senderId","");

            String mapJson = JSON.toJSONString(map);

            log.info("mapJson:{}",mapJson);
            Request postRequest = new Request.Builder().url(url)
                    .addHeader("sign",sign)
                    .addHeader("Timestamp",datetime)
                    .addHeader("Api-Key",appkey).post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), mapJson)).build();

            Response response = okHttpClient.newCall(postRequest).execute();

            log.info("onbukaresponse:{}", response);

            if (response.isSuccessful()) {
                String json = response.body().string();
                log.info("onbukaresponse json: " + json);
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("status").asText();
                flag = false;
                log.info("onbuka status:{},{}",status,"0".equals(status));
                if ("0".equals(status)) {
                    String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
                    log.info("redis: key:{}",key);
                    redisTemplate.opsForValue().set(key, code);
                    log.info("get value:{}",redisTemplate.opsForValue().get(key));
                    redisTemplate.expire(key, 60, TimeUnit.SECONDS);
                    return true;
                }
                String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
                redisTemplate.opsForValue().set(key, code);
                redisTemplate.expire(key, 60, TimeUnit.SECONDS);
                return true;
            }
        } catch (Exception e) {
            log.error("onbuka:{}",e);
            throw new ServerException(1032);
        }
        if (flag) {
            log.info("xxxxxx----> flag");
            String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
            redisTemplate.opsForValue().set(key, code);
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }
        return true;
    }

}
