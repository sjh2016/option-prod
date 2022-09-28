package com.waben.option.common.configuration.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "web.config")
public class WebConfigProperties {

    private List<String> anon = new ArrayList<>();
    
    private List<String> anonGateway = new ArrayList<>();

    private Set<String> ignoreFlowCmdSet = new HashSet<>();

    private int maxRequestLimitCount;

    private WebsocketConfig websocket = new WebsocketConfig();

    private boolean authServer;

    private boolean openFlowLog;

    private boolean storeFlowLog;

    private String logQueue;

    private boolean enable;

    @Data
    public static class WebsocketConfig {

        private String host;

        private int port;

        private int readIdleTime;

        private int ioThreadNum;

        private int backlog;

        private int lowMark;

        private int highMark;

        private boolean responseDataCompress;

    }

}
