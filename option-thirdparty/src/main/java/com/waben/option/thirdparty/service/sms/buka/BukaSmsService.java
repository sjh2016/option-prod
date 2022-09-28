package com.waben.option.thirdparty.service.sms.buka;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class BukaSmsService extends AbstractBaseSmsService {

	@Value("${sms.buka.accessKey:cs_8sfgsc}")
	private String accessKey;

	@Value("${sms.buka.secret:1iEvoRHK}")
	private String secretKey;

	@Value("${sms.buka.secret:cs_8sfgsc}")
	private String appId;

	@Value("${sms.buka.body:Dear customers, Your verification code is: %s}")
	private String smsBody;

	@Value("${sms.buka.expiredSeconds:300}")
	private int expiredSeconds;

	@Value("${sms.buka.url:https://api.onbuka.com/v3/sendSms}")
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
			String msg = String.format(smsBody, code);
			long timestamp = System.currentTimeMillis() / 1000;
			String sign = EncryptUtil.getMD5(accessKey + secretKey + timestamp).toLowerCase();
			// 请求参数
			Map<String, String> params = new HashMap<>();
			params.put("appId", appId);
			params.put("numbers", areaCode + phone);
			params.put("content", msg);
			// 发送请求
			String paramString = JacksonUtil.encode(params);
			log.info("buka sms param: {}", paramString);
			Request postRequest = new Request.Builder().url(url)
					.addHeader("Content-Type", "application/json;charset=UTF-8").addHeader("Sign", sign)
					.addHeader("Timestamp", String.valueOf(timestamp)).addHeader("Api-Key", accessKey)
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			if (response.isSuccessful()) {
				String json = response.body().string();
				log.info("buka sms response: " + json);
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("status").asText();
				log.info("buka sms sendMessage:{}|{}|{}|{}|{}|{}", areaCode, phone, code, expiredSeconds, msg, json);
				if ("0".equals(status)) {
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
