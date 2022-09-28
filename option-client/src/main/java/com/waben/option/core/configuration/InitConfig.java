package com.waben.option.core.configuration;

import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.service.DispatcherMessageService;
import com.waben.option.common.thread.StandardThreadExecutor;
import com.waben.option.common.web.socket.WebSocketServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class InitConfig implements InitializingBean {

    @Resource
    private WebConfigProperties webConfigProperties;

    @Bean
    public WebSocketServer webSocketServer() {
        WebConfigProperties.WebsocketConfig webSocketConfig = webConfigProperties.getWebsocket();
        WebSocketServer webSocketServer = new WebSocketServer(webSocketConfig.getHost(), webSocketConfig.getPort());
        return webSocketServer;
    }

    @Bean("coreThreadExecutor")
    public StandardThreadExecutor standardThreadExecutor(@Value("${threadPool.maxThreads:200}") int maxThreads,
                                                         @Value("${threadPool.minSpareThreads:25}") int minSpareThreads,
                                                         @Value("${threadPool.maxIdleTime:60000}") int maxIdleTime,
                                                         @Value("${threadPool.maxQueueSize:0x7fffffff}") int maxQueueSize) {
        StandardThreadExecutor executor = new StandardThreadExecutor();
        executor.setMaxThreads(maxThreads);
        executor.setMinSpareThreads(minSpareThreads);
        executor.setMaxIdleTime(maxIdleTime);
        executor.setMaxQueueSize(maxQueueSize);
        return executor;
    }

    @Bean
    public DispatcherMessageService dispatcherMessageService() {
        return new DispatcherMessageService();
    }

    @Override
    public void afterPropertiesSet() {
    }
}
