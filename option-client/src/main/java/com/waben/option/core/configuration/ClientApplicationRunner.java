package com.waben.option.core.configuration;

import com.waben.option.common.web.socket.WebSocketServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ClientApplicationRunner implements ApplicationRunner {

    @Resource
    private WebSocketServer webSocketServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        webSocketServer.start();
    }

}
