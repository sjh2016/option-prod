package com.waben.option.common.component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CmdContext {

    private static final ThreadLocal<String> resource = new ThreadLocal<>();

    public static void set(String cmd) {
        resource.set(cmd);
    }

    public static String getCmd() {
        String cmd = resource.get();
        if (cmd == null) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(servletRequestAttributes != null) {
                cmd = servletRequestAttributes.getRequest().getRequestURI();
            }
        }
        return cmd;
    }

    public static void remove() {
        resource.remove();
    }

}
