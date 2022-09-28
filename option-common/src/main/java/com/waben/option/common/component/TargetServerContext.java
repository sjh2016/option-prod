package com.waben.option.common.component;

public class TargetServerContext {

    private static final InheritableThreadLocal<String> resource = new InheritableThreadLocal<>();

    public static void set(String targetServer) {
        resource.set(targetServer);
    }

    public static String getTargetServer() {
        return resource.get();
    }

    public static void remove() {
        resource.remove();
    }

}
