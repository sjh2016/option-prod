package com.waben.option.thirdparty.service.sms.smgw;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author: Peter
 * @date: 2021/7/13 15:38
 */
@Slf4j
@Service
public class SmgwSmsService extends AbstractBaseSmsService {

	@Value("${sms.smgw.accessKey:700001}")
	private String accessKey;

	@Value("${sms.smgw.secret:8JGjerglc}")
	private String secretKey;

	// @Value("${sms.smgw.body:Dear customers, Your verification code is: %s}")
	@Value("${sms.smgw.body:Pelanggan yang terhormat, kode vervikasi Anda adalah: %s}")
	private String smsBody;

	@Value("${sms.smgw.expiredSeconds:300}")
	private int expiredSeconds;

	@Value("${sms.smgw.url:http://45.249.95.122:8090/http/Default.aspx}")
	private String url;

	@Resource
	private OkHttpClient okHttpClient;

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
			msg = URLEncoder.encode(msg, "UTF-8");
			Map<String, String> baseMap = new HashMap<>();
			baseMap.put("action", "send");
			baseMap.put("account", accessKey);
			baseMap.put("password", secretKey);
			baseMap.put("mobile",
					StringUtils.deleteWhitespace(areaCode + (phone.startsWith("0") ? phone.substring(1) : phone)));
			baseMap.put("content", msg);
			// baseMap.put("extno", "1069012345");
			baseMap.put("extno", "MxVerify");
			baseMap.put("rt", "json");
			String queryString = mapToQueryString(baseMap);
			log.info("smgw sms param: {}", queryString);
			Request postRequest = new Request.Builder()
					.url(url).post(RequestBody
							.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
					.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("smgw sms response: " + json);
			if (json.indexOf("\"status\":,") >= 0) {
				json = json.replace("\"status\":,", "\"status\":");
			}

			log.info("sendMessage:{}|{}|{}|{}|{}|{}", queryString, areaCode, phone, code, expiredSeconds, msg);
			JsonNode jsonNode = JacksonUtil.decodeToNode(json);
			String status = jsonNode.get("status").asText();
			if ("0".equals(status)) {
				String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, phone);
				redisTemplate.opsForValue().set(key, code);
				redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
				return true;
			}
		} catch (Exception e) {
			log.error("", e);
		}
		throw new ServerException(1032);
	}

	private String mapToQueryString(Map<String, String> map) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
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
		return builder.toString();
	}
}
