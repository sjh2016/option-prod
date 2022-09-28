package com.waben.option.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Peter
 * @date: 2021/4/27 16:51
 */
@Slf4j
public class IpUtil {

    public static String getIp() {
        String address = null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String ipAddresses = request.getHeader("X-Forwarded-For"); // X-Forwarded-For：Squid 服务代理
            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// Proxy-Client-IP：apache 服务代理
                ipAddresses = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// WL-Proxy-Client-IP：weblogic 服务代理
                ipAddresses = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// HTTP_CLIENT_IP：有些代理服务器
                ipAddresses = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// X-Real-IP：nginx服务代理
                ipAddresses = request.getHeader("X-Real-IP");
            }
            if (ipAddresses != null && ipAddresses.length() != 0) {// 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
                address = ipAddresses.split(",")[0];
            }
            if (address == null || address.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// 还是不能获取到，最后再通过request.getRemoteAddr();获取
                address = request.getRemoteAddr();
            }
        }
        return address;
    }
}
