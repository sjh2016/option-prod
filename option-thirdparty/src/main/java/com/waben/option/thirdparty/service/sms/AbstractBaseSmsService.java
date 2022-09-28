package com.waben.option.thirdparty.service.sms;

import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.thirdparty.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractBaseSmsService implements SmsService {

    private static final Map<String, Integer> requestCountMap = new ConcurrentHashMap<>();
    @Autowired
    protected StringRedisTemplate redisTemplate;
    private volatile long lastCountTime = System.currentTimeMillis();
    @Value("${max.request.limit.count:3}")
    private int maxRequestLimitCount;
    @Value("${max.request.limit.millis:60000}")
    private Long maxRequestLimitMillis;
//    protected RedisTemplate<Serializable, Object> redisTemplate;

    @Override
    public boolean verifyCode(String mobilePhone, String code) {
        String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, mobilePhone);
        String value = redisTemplate.opsForValue().get(key);
        log.info("验证查询到的验证码[{}],用户验证码[{}]", value, code);
        if (value == null || !value.equals(code)) {
            throw new ServerException(1031);
        }
        return true;
    }

    @Override
    public boolean deleteCode(String mobilePhone) {
        String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, mobilePhone);
        redisTemplate.delete(key);
        return true;
    }

    @Override
    public String queryCode(Long currentUserId, String mobilePhone) {
        String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, mobilePhone);
        String value = redisTemplate.opsForValue().get(key);
        log.info("查询到的验证码[{}]", value);
        return value;
    }

    @Override
    public boolean sendCode(String areaCode, String mobilePhone, String code, String content, String ip) {
        return false;
    }

    @Override
    public boolean sendCode(String toEmail, String code, EmailTypeEnum type, String content, String ip) {
        return false;
    }

    private synchronized void resetRequestCount(String ip) {
        Long time = System.currentTimeMillis() - lastCountTime;
        if (time.compareTo(maxRequestLimitMillis) > 0) {
            requestCountMap.clear();
            lastCountTime = System.currentTimeMillis();
            verifyRequestCount(ip);
        }
    }

    public void verifyRequestCount(String ip) {
        if (ip == null) return;
        Long time = System.currentTimeMillis() - lastCountTime;
        if (time.compareTo(maxRequestLimitMillis) <= 0) {
            int count = requestCountMap.getOrDefault(ip, 0);
            if (count >= maxRequestLimitCount) {
                throw new ServerException(1058);
            }
            requestCountMap.put(ip, count + 1);
        } else {
            resetRequestCount(ip);
        }
    }
}
