package com.waben.option.common.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.waben.option.common.component.ChannelContext;
import io.netty.channel.Channel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.Response;
import com.waben.option.common.web.controller.filter.jwt.JWTAuthenticatedUserPrincipal;
import com.waben.option.common.web.socket.ChannelCache;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public abstract class AbstractBaseController {

    @Resource
    protected HttpServletRequest request;
    
    @Resource
    private ChannelCache channelCache;

    public ResponseEntity<Response<Void>> ok() {
        return ResponseEntity.ok(new Response<Void>(0, "", null));
    }

    public <T> ResponseEntity<Response<T>> ok(T data) {
        return ResponseEntity.ok(new Response<T>(0, "", data));
    }

    public Long getCurrentUserId() {
        return getCurrentAuthenticatedUser().getUid();
    }
    
    public String getPlatform() {
        return getCurrentAuthenticatedUser().getPlatform();
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof JWTAuthenticatedUserPrincipal) {
                JWTAuthenticatedUserPrincipal authenticatedUser = (JWTAuthenticatedUserPrincipal) principal;
                return authenticatedUser.getUid();
            }
        }
        return null;
    }
    
    public String getUserIp() {
    	return channelCache.getIp();
    }

    public JWTAuthenticatedUserPrincipal getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof JWTAuthenticatedUserPrincipal) {
                JWTAuthenticatedUserPrincipal authenticatedUser = (JWTAuthenticatedUserPrincipal) principal;
                return authenticatedUser;
            }
            if (authentication.getClass().getSimpleName().indexOf("Anonymous") < 0) {
                log.error("Unknown authentication encountered, ignore it. " + authentication);
            }
        }
        throw new ServerException(1007);
    }

    protected String getIpAddress() {
        String ip = null;
        Channel channel = ChannelContext.getCurrentChannel();
        if(channel != null) {
            InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
            return address.getAddress().getHostAddress();
        } else {
            if (request != null) {
                String ipAddresses = request.getHeader("X-Forwarded-For"); // X-Forwarded-For???Squid ????????????
                if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// Proxy-Client-IP???apache ????????????
                    ipAddresses = request.getHeader("Proxy-Client-IP");
                }
                if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// WL-Proxy-Client-IP???weblogic ????????????
                    ipAddresses = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// HTTP_CLIENT_IP????????????????????????
                    ipAddresses = request.getHeader("HTTP_CLIENT_IP");
                }
                if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// X-Real-IP???nginx????????????
                    ipAddresses = request.getHeader("X-Real-IP");
                }
                if (ipAddresses != null && ipAddresses.length() != 0) {// ???????????????????????????????????????????????????ip?????????????????????????????????????????????,?????????????????????????????????ip?????????????????????IP
                    ip = ipAddresses.split(",")[0];
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// ???????????????????????????????????????request.getRemoteAddr();??????
                    ip = request.getRemoteAddr();
                }
            }
        }
        return ip;
    }

}

