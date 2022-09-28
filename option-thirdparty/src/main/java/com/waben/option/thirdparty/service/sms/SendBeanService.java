package com.waben.option.thirdparty.service.sms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.model.dto.sms.SendBeanDTO;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.util.PatternUtil;
import com.waben.option.thirdparty.service.RandomService;
import com.waben.option.thirdparty.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SendBeanService {

    @Resource
    private ConfigAPI configAPI;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    protected RedisTemplate<Serializable, Object> redisTemplate;

    private static List<RandomService> emailExercises = new ArrayList<>();
    //    private static final List<RandomService> smsExercises = new ArrayList<>();
    private static final Random rand = new Random();

    @Value("${sms.bean.name:globalSmsService}")
    private String smsBeanName;

    @Value("${email.bean.name:winterLauEmailService,daiHunEmailService}")
    private String emailBeanName;

    @Value("${email.send.count:2000}")
    private BigDecimal emailSendCount;

    public synchronized void sendEmail(String username, String code, EmailTypeEnum type, String content, String ip) {
        if (!PatternUtil.isEmail(username)) throw new ServerException(1022);
        List<SendBeanDTO> beanDTOList = getEmailBeanListConfig();
        if (!CollectionUtils.isEmpty(beanDTOList)) {
            for (SendBeanDTO emailBean : beanDTOList) {
                String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_EMAIL_COUNT, emailBean.getSendBeanName());
                Integer finalCount = getRedisCount(key);
                int limitCount = emailBean.getMaxLimitCount().multiply(emailBean.getRate()).intValue();
                log.info("emailBean|{}|{}|{}", emailBean.getSendBeanName(), finalCount, limitCount);
                if (finalCount > limitCount) continue;
                emailExercises = new ArrayList<>();
                emailExercises.add(new RandomService() {
                    @Override
                    public void run() {
                        SpringContext.getBean(emailBean.getSendBeanName(), SmsService.class).sendCode(username, code, type, content, ip);
                        int emailCount = finalCount;
                        redisTemplate.opsForValue().set(key, ++emailCount);
                        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
                        long seconds = duration.getSeconds();
                        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
                    }
                });
                break;
            }
            emailExercises.get(rand.nextInt(emailExercises.size())).run();
        }
    }

    private List<SendBeanDTO> getEmailBeanListConfig() {
        try {
            String emailBeansConfig = configAPI.querySendBeanConfig(DBConstants.CONFIG_SEND_EMAIL_BEAN_KEY);
            if (!StringUtils.isEmpty(emailBeansConfig)) {
                return objectMapper.readValue(emailBeansConfig, new TypeReference<List<SendBeanDTO>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getRedisCount(String key) {
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        if (count == null) count = 0;
        return count;
    }

    public void sendSms(String areaCode, String username, String code, String content, String ip) {
        if (username.startsWith("0")) username = username.substring(1);
        if (smsBeanName != null) {
            SpringContext.getBean(smsBeanName, SmsService.class).sendCode(areaCode, username, code, content, ip);
            /*List<RandomService> smsExercises = new ArrayList<>();
            String[] smsService = smsBeanName.split(",");
            for (String smsBean : smsService) {
                smsExercises.add(new RandomService() {
                    @Override
                    public void run() {
                        SpringContext.getBean(smsBean, SmsService.class).sendCode(areaCode, username, code, content, ip);
                    }
                });
            }
            smsExercises.get(rand.nextInt(smsExercises.size())).run();*/
        }
    }


}
