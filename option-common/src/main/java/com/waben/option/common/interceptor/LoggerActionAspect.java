package com.waben.option.common.interceptor;

import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.LoggerMessage;
import com.waben.option.common.component.CmdContext;
import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.web.controller.filter.jwt.JWTAuthenticatedUserPrincipal;
import com.waben.option.common.web.socket.ChannelCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class LoggerActionAspect {

    public final static Logger flowLog = LoggerFactory.getLogger("Flow");

    @Resource
    private WebConfigProperties webConfigProperties;

    @Resource
    private AMQPService amqpService;

    @Resource
    private ChannelCache channelCache;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void execute() {
    }

    @Around("execute()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        boolean isSuccess = true;
        Signature sig = pjp.getSignature();
        MethodSignature signature = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
        Object[] args = pjp.getArgs();
        Parameter[] parameters = method.getParameters();
        String cmd = CmdContext.getCmd();
        Map<String, Object> paramMap = getMethodArgument(args, parameters);
        long startTime = System.currentTimeMillis();
        Integer errorCode = null;
        try {
            return pjp.proceed();
        } catch (Exception e) {
            isSuccess = false;
            if (e instanceof ServerException) {
                errorCode = ((ServerException) e).getCode();
            }
            throw e;
        } finally {
            if (webConfigProperties.isOpenFlowLog()) {
                if (!webConfigProperties.getIgnoreFlowCmdSet().contains(cmd)) {
                    long useTime = System.currentTimeMillis() - startTime;
                    Long userId = null;
                    JWTAuthenticatedUserPrincipal authentication = getJWTAuthenticatedUserPrincipal();
                    if (authentication != null) {
                        userId = authentication.getUid();
                    }
                    String ip = channelCache.getIp();
                    flowLog.info("{}|{}|{}|{}|{}|{}", cmd, ip, userId, isSuccess, useTime, paramMap);
                    if (amqpService != null && webConfigProperties.isStoreFlowLog()) {
                        boolean isVerify = true;
                        for (Object value : paramMap.values()) {
                            if (value != null && (value instanceof HttpServletRequest || value instanceof MultipartFile)) {
                                isVerify = false;
                                break;
                            }
                        }
                        if (isVerify) {
                            try {
                                amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, webConfigProperties.getLogQueue(),
                                        new AMQPMessage<>(new LoggerMessage(userId, cmd, ip, paramMap, LocalDateTime.now(), errorCode)));
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private JWTAuthenticatedUserPrincipal getJWTAuthenticatedUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof JWTAuthenticatedUserPrincipal) {
                return (JWTAuthenticatedUserPrincipal) principal;
            }
        }
        return null;
    }

    private Map<String, Object> getMethodArgument(Object[] args, Parameter[] parameters) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (parameters != null && parameters.length == 1 && parameters[0].getAnnotation(RequestBody.class) != null) {
            Class<?> clazz = args[0].getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (!field.getName().contains("password")) {
                        paramMap.put(field.getName(), field.get(args[0]));
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        } else {
            if (parameters != null && parameters.length > 0) {
                for (int i = 0; i < parameters.length; i++) {
                    paramMap.put(parameters[i].getName(), args[i]);
                }
            }
        }
        return paramMap;
    }

}
