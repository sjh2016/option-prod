package com.waben.option.common.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        if(SpringContext.applicationContext == null) {
            SpringContext.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new RuntimeException("组件未初始化");
        }
        return applicationContext;
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    public static String getProperty(String key) {
        return SpringContext.getBean(Environment.class).getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return SpringContext.getBean(Environment.class).getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return SpringContext.getBean(Environment.class).getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return SpringContext.getBean(Environment.class).getProperty(key, targetType, defaultValue);
    }

}