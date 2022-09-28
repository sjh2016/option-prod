package com.waben.option.thirdparty.service.sms.smscloud;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Service
public class SmsCloudSmsService extends AbstractBaseSmsService {

	@Value("${sms.cloud.accessKey:Bhg9kw}")
	private String accessKey;

	@Value("${sms.cloud.secret:LrPmav}")
	private String secretKey;

	@Value("${sms.cloud.appCode:1000}")
	private String appCode;

	@Value("${sms.cloud.body:Dear customers, Your verification code is: %s}")
	private String smsBody;

	@Value("${sms.cloud.expiredSeconds:300}")
	private int expiredSeconds;

	@Value("${sms.cloud.url:http://47.242.85.7:9090/sms/batch/v2}")
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
			String fullPhone = StringUtils.deleteWhitespace(areaCode + phone);
			String msg = String.format(smsBody, code);
			String fillUrl = url + "?appkey=" + accessKey + "&appsecret=" + secretKey + "&phone=" + fullPhone + "&msg="
					+ URLEncoder.encode(msg, "UTF-8") + "&appcode=" + appCode;
			log.info("smscloud sms param: {}", fillUrl);
			Request getRequest = new Request.Builder().url(fillUrl).get().build();
			Response response = okHttpClient.newCall(getRequest).execute();
			if (response.isSuccessful()) {
				String json = response.body().string();
				log.info("smscloud sms response: " + json);
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("code").asText();
				log.info("sendMessage:{}|{}|{}|{}|{}|{}", areaCode, phone, code, expiredSeconds, msg, json);
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
