package com.waben.option.thirdparty.service.sms.huaweiyun;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HuaWeiSmsService extends AbstractBaseSmsService {

    private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";
    private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    @Value("${sms.huawei.accessKey:s0S11px0kbO7Dp366N1yHtm4Vh0B}")
    private String accessKey;

    @Value("${sms.huawei.secret:bbF1F5mXZ6VdC6TfNaU2HU8ZWOX7}")
    private String secretKey;

    @Value("${sms.huawei.expiredSeconds:300}")
    private int expiredSeconds;

    @Value("${sms.huawei.senderNumber:isms0000000158}")
    private String senderNumber;

    @Value("${sms.huawei.templateId:834badbc64364d67a596c447a2811d4d}")
    private String templateId;

    @Value("${sms.huawei.url:https://rtcsms.ap-southeast-1.myhuaweicloud.com:443/sms/batchSendSms/v1}")
    private String url;

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;

    @Override
    public boolean sendCode(String areaCode, String phone, String code, String content, String ip) {
        verifyRequestCount(ip);
        try {
            String templateParas = JSONObject.toJSONString(new String[]{code});
            String body = buildRequestBody(senderNumber, "+" + areaCode + phone, templateId, templateParas, null, null);
            Request postRequest = new Request.Builder()
                    .url(url).post(RequestBody
                            .create(MediaType.parse("application/x-www-form-urlencoded"), body)).addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                    .addHeader("X-WSSE", buildWsseHeader(accessKey, secretKey)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("sendMessage:{}|{}|{}|{}|{}", areaCode, phone, code, expiredSeconds, json);
            JsonNode jsonNode = JacksonUtil.decodeToNode(json);
            String reqCode = jsonNode.get("code").asText();
            if ("000000".equals(reqCode)) {
                String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
                redisTemplate.opsForValue().set(key, code);
                redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                return true;
            }
            String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
            redisTemplate.opsForValue().set(key, code);
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ServerException(1032);
    }

    private static String buildRequestBody(String sender, String receiver, String templateId, String templateParas,
                                           String statusCallbackUrl, String signature) {
        List<NameValuePair> keyValues = new ArrayList<NameValuePair>();
        keyValues.add(new BasicNameValuePair("from", sender));
        keyValues.add(new BasicNameValuePair("to", receiver));
        keyValues.add(new BasicNameValuePair("templateId", templateId));
        if (null != templateParas && !templateParas.isEmpty()) {
            keyValues.add(new BasicNameValuePair("templateParas", templateParas));
        }
        if (null != statusCallbackUrl && !statusCallbackUrl.isEmpty()) {
            keyValues.add(new BasicNameValuePair("statusCallback", statusCallbackUrl));
        }
        if (null != signature && !signature.isEmpty()) {
            keyValues.add(new BasicNameValuePair("signature", signature));
        }
        return URLEncodedUtils.format(keyValues, StandardCharsets.UTF_8);
    }

    private static String buildWsseHeader(String appKey, String appSecret) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String time = sdf.format(new Date());
        String nonce = UUID.randomUUID().toString().replace("-", "");
        byte[] passwordDigest = DigestUtils.sha256(nonce + time + appSecret);
        String hexDigest = Hex.encodeHexString(passwordDigest);
        String passwordDigestBase64Str = Base64.getEncoder().encodeToString(hexDigest.getBytes());
        return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }

}
