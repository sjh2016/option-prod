package com.waben.option.core.configuration;

import com.waben.option.core.thread.OutsidePushMessageQueue;
import com.waben.option.core.thread.PushMessageQueueManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig implements InitializingBean {

    @Value("${thread.message.push.count:1}")
    private int threadCount;

    @Bean
    public PushMessageQueueManager pushMessageQueueManage() {
        return new PushMessageQueueManager(threadCount);
    }

    @Bean
    public OutsidePushMessageQueue outsidePushMessageQueue() {
        return new OutsidePushMessageQueue();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        pushMessageQueueManage().init();
        outsidePushMessageQueue().start();
    }

}
