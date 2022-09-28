package com.waben.option.thirdparty.service.sms.sms230;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RefreshScope
@Service
public class Sms230SmsService extends AbstractBaseSmsService {

    @Value("${sms.sms230.accessKey:vISJs7j6f4PBtd5GTViK5w==}")
    private String accessKey;

    @Value("${sms.sms230.secret:3d446cf92e034357ab868264d30e3ea4}")
    private String secretKey;

    @Value("${sms.sms230.body:Your verification code is %s,Valid for 5 minutes.}")
    private String smsBody;

    @Value("${sms.sms230.expiredSeconds:300}")
    private int expiredSeconds;

    @Value("${sms.sms230.senderid}")
    private String senderid;

    @Value("${sms.sms230.templateid}")
    private String templateid;

    @Value("${sms.sms230.signname}")
    private String signname;

    @Value("${sms.sms230.url:https://api.230sms.com/outauth/verifCodeSend}")
    private String url;

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;

    @Override
    public boolean sendCode(String areaCode, String phone, String code, String content, String ip) {
        verifyRequestCount(ip);
        try {
            if (phone.startsWith("0")) {
                phone = phone.substring(1);
            }
            String msg = String.format(smsBody, code);
            String dateTime = LocalDateTime.now()
//                    .plusHours(2).plusMinutes(30)
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String sign = EncryptUtil.getMD5(accessKey + dateTime + secretKey).toLowerCase();
            // 构建参数
            Map<String, String> baseMap = new HashMap<>();
            baseMap.put("apikey", accessKey);
            baseMap.put("timestamp", dateTime);
            baseMap.put("mobile", StringUtils.deleteWhitespace(areaCode + phone));
            baseMap.put("content", msg);
            if (StringUtils.isNotBlank(senderid)) {
                baseMap.put("senderid", senderid);
            }
            if (StringUtils.isNotBlank(templateid)) {
                baseMap.put("templateid", templateid);
            }
            if (StringUtils.isNotBlank(signname)) {
                baseMap.put("signname", signname);
            }
            baseMap.put("sign", sign);
            String queryString = JacksonUtil.encode(baseMap);
            log.info("sms230 sms param: {}", queryString);
            Request postRequest = new Request.Builder().url(url)
                    .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), queryString)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                log.info("sms230 sms response: " + json);
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("status").asText();
                log.info("sendMessage:{}|{}|{}|{}|{}|{}", areaCode, phone, code, expiredSeconds, msg, json);
                if ("000".equals(status)) {
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
