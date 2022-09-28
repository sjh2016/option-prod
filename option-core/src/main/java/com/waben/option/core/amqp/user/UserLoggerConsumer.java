package com.waben.option.core.amqp.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.LoggerMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.message.MessageFactory;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.data.entity.resource.LoggerCommand;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserLogger;
import com.waben.option.data.repository.resource.LoggerCommandDao;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserLoggerDao;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_LOGGER_USER)
public class UserLoggerConsumer extends BaseAMPQConsumer<LoggerMessage> {

    @Resource
    private UserLoggerDao userLoggerDao;

    @Resource
    private UserDao userDao;

    @Resource
    private LoggerCommandDao loggerCommandDao;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void handle(LoggerMessage message) {
        Long userId = message.getUserId();
        if (userId != null) {
            User user = userDao.selectById(message.getUserId());
            if (user == null) return;
        } else {
            if (message.getParamMap().get("username") == null) return;
            String username = (String) message.getParamMap().get("username");
            User user = userDao.selectOne(new QueryWrapper<User>().eq(User.USERNAME, username));
            if (user == null) return;
            userId = user.getId();
        }
        LoggerCommand loggerCommand = loggerCommandDao.selectOne(new QueryWrapper<LoggerCommand>().eq(LoggerCommand.CMD, message.getCmd()));
        if (loggerCommand != null) {
            UserLogger logger = new UserLogger();
            logger.setId(idWorker.nextId());
            logger.setUserId(userId);
            logger.setCmd(message.getCmd());
            logger.setCmdName(loggerCommand.getName());
            logger.setIp(message.getIp());
            logger.setParams(message.getParamMap());
            StringBuffer buffer = new StringBuffer();
            if (!CollectionUtils.isEmpty(loggerCommand.getKeyList())) {
                Object[] array = new Object[loggerCommand.getKeyList().size()];
                int i = 0;
                for (String key : loggerCommand.getKeyList()) {
                    array[i++] = getArrayValue(key, message.getParamMap());
                }
                String formatStr = String.format(loggerCommand.getDetail(), array);
                if (!StringUtils.isEmpty(formatStr)) buffer.append(formatStr).append("，");
            }
            buffer.append(loggerCommand.getName());
            if (message.getErrorCode() != null) {
                buffer.append("失败；失败原因：");
                buffer.append(converter(message.getErrorCode())).append("!");
            } else {
                buffer.append("成功！");
            }
            logger.setDetail(buffer.toString());
            logger.setGmtCreate(message.getTime());
            userLoggerDao.insert(logger);
        }
    }

    private Object getArrayValue(String key, Map<String, Object> paramMap) {
        if (key.contains("@")) {
            String[] keys = key.split("@");
            if (keys[1].contains("&")) {
                paramMap = JacksonUtil.decode((String) paramMap.get(keys[0]), Map.class);
                return getParamValue(key, paramMap);
            } else {
                return getResult((List) paramMap.get(keys[0]), keys);
            }
        } else if (key.contains("&")) {
            return getParamValue(key, paramMap);
        } else {
            return paramMap.get(key) == null ? "空" : paramMap.get(key);
        }
    }

    private String getResult(List list, String[] keys) {
        StringBuffer sb = new StringBuffer();
        for (Object object : list) {
            Map objectMap = null;
            try {
                objectMap = objectMapper.readValue(objectMapper.writeValueAsString(object), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            sb.append(objectMap.get(keys[1])).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private Object getParamValue(String key, Map<String, Object> paramMap) {
        String[] keys = key.split("&");
        Map map = JacksonUtil.decode(keys[1], Map.class);
        Object value = paramMap.get(keys[0]);
        return map.get(value.toString()) == null ? "空" : map.get(value.toString());
    }

    private String converter(int code) {
        return MessageFactory.INSTANCE.getMessage(code + "", "zh", "CN");
    }
}
