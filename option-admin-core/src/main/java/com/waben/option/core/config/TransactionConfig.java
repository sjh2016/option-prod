/*
package com.waben.option.core.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Configuration
@Slf4j
public class TransactionConfig {

    @Resource(name = "myRoutingDataSource")
    private DataSource myRoutingDataSource;

    private static final int TX_METHOD_TIMEOUT = 300;
    private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.waben.option.data.repository.BaseRepository.*(..))";

    @Bean(name = "txAdvice")
    public TransactionInterceptor txAdvice() {

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        // 只读事务，不做更新操作
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTx.setTimeout(TX_METHOD_TIMEOUT);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("get*", requiredTx);
        txMap.put("select*", requiredTx);
        txMap.put("find*", requiredTx);
        txMap.put("query*", requiredTx);
//        txMap.put("add*", requiredTx);
//        txMap.put("save*", requiredTx);
//        txMap.put("insert*", requiredTx);
//        txMap.put("create*", requiredTx);
//        txMap.put("update*", requiredTx);
//        txMap.put("batch*", requiredTx);
//        txMap.put("modify*", requiredTx);
//        txMap.put("delete*", requiredTx);
//        txMap.put("remove*", requiredTx);
//        txMap.put("exec*", requiredTx);
//        txMap.put("set*", requiredTx);
//        txMap.put("do*", requiredTx);
//        txMap.put("get*", readOnlyTx);
//        txMap.put("query*", readOnlyTx);
        txMap.put("find*", readOnlyTx);
//        txMap.put("*", requiredTx);
        source.setNameMap(txMap);
        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager(), source);
        return txAdvice;
    }

    @Bean
    public Advisor txAdviceAdvisor(@Qualifier("txAdvice") TransactionInterceptor txAdvice) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }

    */
/**
     * 自定义 事务管理器 管理我们自定义的 MyRoutingDataSource 数据源
     *
     * @return
     *//*

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(myRoutingDataSource);
    }
}
*/
