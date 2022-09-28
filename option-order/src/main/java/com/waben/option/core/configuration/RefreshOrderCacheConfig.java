package com.waben.option.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
public class RefreshOrderCacheConfig {

    @Bean("executorBeanName")
    public ThreadPoolTaskExecutor executorBeanName() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //通过Runtime方法来获取当前服务器cpu内核，根据cpu内核来创建核心线程数和最大线程数
        int threadCount = Runtime.getRuntime().availableProcessors();
        //核心线程数5：线程池创建时候初始化的线程数（核心线程数最大不超过）
        executor.setCorePoolSize(5);
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(10);
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(500);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("async01-");
        //配置拒绝策略，线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//配置拒绝策略
        executor.initialize();
        return executor;
    }

}
