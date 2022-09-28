package com.waben.option.core.service.resource;

import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.model.dto.resource.ImageCodeDTO;
import com.waben.option.common.util.ImageCodeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Service
public class ImageCodeService {

	@Resource
	private RedisTemplate<Serializable, Object> redisTemplate;

	@Resource
	private IdWorker idWorker;

	public ImageCodeDTO generateCode(int n) {
		long sessionId = idWorker.nextId();
		String imageCode = ImageCodeUtil.generateVerifyCode(n);
		String image = ImageCodeUtil.outputImage(80, 32, imageCode);
		String key = RedisKey.OPTION_RESOURCE_IMG_CODE_KEY + sessionId;
		redisTemplate.opsForValue().set(key, imageCode);
		redisTemplate.expire(key, 60, TimeUnit.SECONDS);
		// 前2位 + 后2位 = 验证码
//		return new ImageCodeDTO(sessionId + "", imageCode.substring(0, 2) + image + imageCode.substring(2));
		return new ImageCodeDTO(sessionId + "", image);
	}

	public boolean verifyCode(String sessionId, String code) {
		String key = RedisKey.OPTION_RESOURCE_IMG_CODE_KEY + sessionId;
		String imageCode = (String) redisTemplate.opsForValue().get(key);
		if (code.equalsIgnoreCase(imageCode)) {
			redisTemplate.delete(key);
			return true;
		}
		return false;
	}

	public boolean verifyNotDeleteCode(String sessionId, String code) {
		String key = RedisKey.OPTION_RESOURCE_IMG_CODE_KEY + sessionId;
		String imageCode = (String) redisTemplate.opsForValue().get(key);
		if (code.equalsIgnoreCase(imageCode)) {
			return true;
		}
		return false;
	}

}
