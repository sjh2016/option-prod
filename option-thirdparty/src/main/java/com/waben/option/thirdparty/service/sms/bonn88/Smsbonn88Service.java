package com.waben.option.thirdparty.service.sms.bonn88;

import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@RefreshScope
@Service
public class Smsbonn88Service extends AbstractBaseSmsService {

    @Value("${sms.bonn88.appkey:bonn88}")
    private String appkey;

    @Value("${sms.bonn88.appsecret:lnd888}")
    private String appsecret;

    @Value("${sms.bonn88.appcode:1000}")
    private String appcode;

    @Value("${sms.bonn88.msg:Your Verification Code is %s}")
    private String smsbody;

    @Value("${sms.bonn88.url:http://47.242.85.7:9090/sms/batch/v2?appkey=%s&appsecret=%s&appcode=%s&phone=%s&msg=%s}")
    private String url;

    @Value("${sms.bonn88.expiredSeconds:300}")
    private int expiredSeconds;

//    @Resource
//    private RedisTemplate<Serializable, Object> redisTemplate;

    @Resource
    private OkHttpClient okHttpClient;

    @Override
    public boolean sendCode(String areaCode, String phone, String code, String content, String ip) {
        verifyRequestCount(ip);
        try {
            if (phone.startsWith("0")) {
                phone = phone.substring(1);
            }
            String msg = String.format(smsbody, code);
            String requestUrl = String.format(url, appkey, appsecret, appcode, areaCode + (phone.startsWith("0") ? phone.substring(1) : phone), msg);
            log.info("bonn88发送短信请求地址[{}]", requestUrl);
            Request postRequest = new Request.Builder().url(requestUrl).get().build();
            Response response = okHttpClient.newCall(postRequest).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                log.info("bonn88 sms response: " + json);
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("code").asText();
                if ("00000".equals(status)) {
                    String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
                    redisTemplate.opsForValue().set(key, code);
                    redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                    return true;
                }
                String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
                redisTemplate.opsForValue().set(key, code);
                redisTemplate.expire(key, 60, TimeUnit.SECONDS);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ServerException(1032);
    }

}
