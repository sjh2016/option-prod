package com.waben.option.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.ChannelContext;
import com.waben.option.common.component.CmdContext;
import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.component.RequestParamContext;
import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.constants.Constants;
import com.waben.option.common.exception.JwtAuthenticationException;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.bean.CommandMapping;
import com.waben.option.common.thread.MessageQueue;
import com.waben.option.common.thread.StandardThreadExecutor;
import com.waben.option.common.util.ResponseUtil;
import com.waben.option.common.web.controller.filter.jwt.JWTAuthenticatedUserPrincipal;
import com.waben.option.common.web.controller.filter.jwt.JwtAuthenticationToken;
import com.waben.option.common.web.socket.ChannelCache;
import com.waben.option.common.web.socket.TcpApplication;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
public class DispatcherMessageService {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private JwtService jwtService;

    @Resource
    private WebConfigProperties webConfigProperties;

    @Resource
    @Qualifier("coreThreadExecutor")
    private StandardThreadExecutor executor;

    @Resource
    private ChannelCache channelCache;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final MessageQueue<Runnable> singleMessageQueue = new MessageQueue<Runnable>() {

        @Override
        protected void execute(Runnable message) {
            message.run();
        }

    };

    @PostConstruct
    public void init() {
        executor.start();
        singleMessageQueue.start();
    }

    public void dispatch(Channel channel, byte[] data) {
        Map<String, Object> requestMap;
        try {
            requestMap = objectMapper.readValue(data, Map.class);
        } catch (Exception e) {
            channel.writeAndFlush(ResponseUtil.buildData("", new ServerException(1001), Integer.MAX_VALUE));
            log.error("REQUEST_PARAM|{}", new String(data));
            log.error("", e);
            return;
        }
        final Map<String, Object> paramMap = requestMap;
        String cmd = (String) paramMap.get("cmd");
        String locale = (String) paramMap.get("locale");
        if (locale == null) {
            locale = Constants.DEFAULT_LANGUAGE;
        }
        Integer requestId = (Integer) paramMap.get("requestId");
        Map<String, Object> param;
        if (paramMap.containsKey("param")) {
            param = (Map<String, Object>) paramMap.get("param");
        } else {
            param = new HashMap<>();
        }
        try {
            CommandMapping commandMapping = TcpApplication.getCommandMapping(cmd);
            Runnable runnable = getRunnable(channel, locale, commandMapping, requestId, param);
            if (commandMapping.isSingleExecute()) {
                singleMessageQueue.addMessage(runnable);
            } else {
                executor.execute(runnable);
            }
        } catch (RejectedExecutionException e) {
            log.error("", e);
            channel.writeAndFlush(ResponseUtil.buildData(cmd, new ServerException(1015), requestId));
            return;
        } catch (ServerException e) {
            log.error("ERROR_PARAM: " + requestMap.toString(), e);
            channel.writeAndFlush(ResponseUtil.buildData(cmd, e, requestId));
            return;
        }
    }

    private Runnable getRunnable(Channel channel, String locale, CommandMapping commandMapping, Integer requestId, Map<String, Object> param) {
        return () -> {
            String cmd = commandMapping.getUri();
            try {
                Method method = commandMapping.getMethod();
                Parameter[] parameters = method.getParameters();
                Object[] paramValues = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramValues[i] = getParameterValue(param, parameters[i]);
                }
                verifyChannel(commandMapping.getUri(), channel);
                ChannelContext.set(channel);
                RequestParamContext.set(param);
                LocaleContext.set(locale);
                CmdContext.set(cmd);
                ExecutableValidator executableValidator = validator.forExecutables();
                Set<ConstraintViolation<Object>> validators = executableValidator.validateParameters(commandMapping.getObject(), method, paramValues);
                for (ConstraintViolation<Object> constraintViolation : validators) {
                    throw new ServerException(Integer.parseInt(constraintViolation.getMessage()));
                }
                Object object = method.invoke(commandMapping.getObject(), paramValues);
                ResponseEntity<?> response = (ResponseEntity<?>) object;
                if (response.getBody() instanceof Response) {
                    channel.writeAndFlush(ResponseUtil.buildData(cmd, (Response<?>) response.getBody(), requestId));
                } else {
                    channel.writeAndFlush(ResponseUtil.buildData(cmd, (Response<?>) null, requestId));
                }
            } catch (ServerException e) {
                channel.writeAndFlush(ResponseUtil.buildData(cmd, e, requestId));
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                    if (invocationTargetException.getTargetException() instanceof ServerException) {
                        channel.writeAndFlush(ResponseUtil.buildData(cmd, (ServerException) ((InvocationTargetException) e).getTargetException(), requestId));
                        return;
                    } else if (invocationTargetException.getTargetException() instanceof JwtAuthenticationException) {
                        channel.writeAndFlush(ResponseUtil.buildData(cmd, new ServerException(1008), requestId));
                        return;
                    }
                }
                channel.writeAndFlush(ResponseUtil.buildData(cmd, new ServerException(1000), requestId));
                log.error("", e);
            } finally {
                SecurityContextHolder.clearContext();
                ChannelContext.remove();
                RequestParamContext.remove();
                LocaleContext.remove();
                CmdContext.remove();
            }
        };
    }

    private boolean verifyChannel(String uri, Channel channel) {
        if (!webConfigProperties.isAuthServer()) {
            return true;
        }
        List<String> anonList = webConfigProperties.getAnon();
        boolean needVerify = true;
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        for (String anon : anonList) {
            if (anon.equals(uri)) {
                needVerify = false;
                break;
            } else {
                if (anon.endsWith("/**")) {
                    if (uri.startsWith(anon.substring(0, anon.length() - 2))) {
                        needVerify = false;
                    }
                }
            }
        }
        String jwt = channelCache.getToken(channel);
        if (needVerify) {
            Claims claims;
            if (StringUtils.isBlank(jwt)) {
                throw new ServerException(1007);
            }
            try {
                claims = jwtService.verify(jwt);
            } catch (AuthenticationException e) {
                if (needVerify) {
                    throw new ServerException(1008);
                } else {
                    return true;
                }
            }
            JWTAuthenticatedUserPrincipal principal = new JWTAuthenticatedUserPrincipal(claims);
            JwtAuthenticationToken token = new JwtAuthenticationToken(principal, jwt, null);
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        return true;
    }

    private Object getParameterValue(Map<String, Object> param, Parameter parameter) {
        RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
        try {
            if (requestBody != null) {
                if (param == null && requestBody.required()) {
                    throw new ServerException(1002);
                }
                return objectMapper.readValue(objectMapper.writeValueAsString(param), parameter.getType());
            } else {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    String name = requestParam.name().trim().equals("") ? parameter.getName() : requestParam.name();
                    Object value = param.get(name);
                    if (value == null) {
                        if (requestParam.required()) {
                            throw new ServerException(1002);
                        } else {
                            return null;
                        }
                    } else {
                        return getValue(parameter, value, parameter.getType());
                    }
                } else {
                    Object value = param.get(parameter.getName());
                    return getValue(parameter, value, parameter.getType());
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new ServerException(1001);
        }
    }

    private Object getValue(Parameter parameter, Object value, Class<?> type)
            throws Exception {
        if (value == null) {
            return null;
        }
        if (type.isEnum()) {
            return getEnumObject((String) value, type);
        }
        String typeName = type.getSimpleName();
        switch (typeName) {
            case "long":
            case "Long":
                if (value != null && value instanceof String) {
                    if ("".equals(value)) {
                        return null;
                    } else {
                        return Long.parseLong(value.toString());
                    }
                } else {
                    return value;
                }
            case "int":
            case "Integer":
            case "short":
            case "Short":
            case "double":
            case "Double":
            case "char":
            case "Character":
            case "float":
            case "Float":
            case "byte":
            case "Byte":
            case "boolean":
            case "Boolean":
                return value;
            default:
                return objectMapper.readValue(objectMapper.writeValueAsString(value), parameter.getType());
        }
    }

    private Object getEnumObject(String value, Class<?> clazz) throws Exception {
        Method method = clazz.getMethod("valueOf", String.class);
        return method.invoke(clazz, value);
    }

}
