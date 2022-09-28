package com.waben.option.thirdparty.service.sms.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GlobalSmsService extends AbstractBaseSmsService {

	@Value("${sms.global.accessKey:cs_8sfgsc}")
	private String accessKey;

	@Value("${sms.global.secret:1iEvoRHK}")
	private String secretKey;

	@Value("${sms.global.body:Pelanggan yang terhormat, kode vervikasi Anda adalah: %s}")
	private String smsBody;

	@Value("${sms.global.expiredSeconds:300}")
	private int expiredSeconds;

	@Value("${sms.global.url:http://sms.skylinelabs.cc:20003/sendsmsV2?account=%s&sign=%s&datetime=%s&senderid=&numbers=%s&content=%s}")
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
			if(phone.startsWith("0")) {
				phone = phone.substring(1);
			}
			String msg = String.format(smsBody, code);
			String dateTime = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			String sign = EncryptUtil.getMD5(accessKey + secretKey + dateTime).toLowerCase();
			String getUrl = String.format(url, accessKey, sign, dateTime,
					areaCode + (phone.startsWith("0") ? phone.substring(1) : phone), URLEncoder.encode(msg, "UTF-8"));
			log.info("global sms param: {}", getUrl);
			Request request = new Request.Builder().url(getUrl)
					.addHeader("Content-Type", "application/json;charset=UTF-8").build();
			Response response = okHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String json = response.body().string();
				log.info("global sms response: " + json);
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("status").asText();
				log.info("sendMessage:{}|{}|{}|{}|{}|{}", areaCode, phone, code, expiredSeconds, msg, json);
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
