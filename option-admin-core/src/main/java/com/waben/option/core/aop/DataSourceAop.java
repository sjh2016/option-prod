/*
package com.waben.option.core.aop;

import com.waben.option.core.bean.DBContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DataSourceAop {

   */
/* @Pointcut("!@annotation(com.waben.option.common.annotation.Master) " +
            "&& execution(* com.waben.option.data.repository.BaseRepository.select*(..))")
    public void readPointcut() {

    }

    @Pointcut("@annotation(com.waben.option.common.annotation.Master) " +
            "|| execution(* com.waben.option.data.repository.BaseRepository.insert*(..)) " +
            "|| execution(* com.waben.option.data.repository.BaseRepository.delete*(..)) " +
            "|| execution(* com.waben.option.data.repository.BaseRepository.update*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        DBContextHolder.slave();
    }

    @Before("writePointcut()")
    public void write() {
        DBContextHolder.master();
    }*//*


    @Before("execution(* com.waben.option.data.repository.BaseRepository.*(..))")
    public void before(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        if (StringUtils.startsWithAny(methodName, new String[]{"get", "select", "find", "query"})) {
            DBContextHolder.slave();
        } else {
            DBContextHolder.master();
        }
    }

    */
/**
     * 执行完切面后，将线程共享中的数据源名称清空，
     * 数据源恢复为原来的默认数据源
     *//*

    @After("execution(* com.waben.option.data.repository.BaseRepository.*(..))")
    public void after(JoinPoint joinPoint) {
        DBContextHolder.remove();
    }
}
*/
