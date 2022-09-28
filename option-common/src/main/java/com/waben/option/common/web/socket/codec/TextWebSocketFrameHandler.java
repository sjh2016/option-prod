package com.waben.option.common.web.socket.codec;

import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.service.DispatcherMessageService;
import com.waben.option.common.util.ResponseUtil;
import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class TextWebSocketFrameHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private DispatcherMessageService dispatcherMessageService;

    @Resource
    private WebConfigProperties webConfigProperties;

    private volatile long lastCountTime = System.currentTimeMillis();

    private final Map<Channel, Integer> requestCountMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof TextWebSocketFrame) {
                if (!verifyRequestCount(ctx.channel())) return;
                TextWebSocketFrame frame = (TextWebSocketFrame) msg;
                dispatcherMessageService.dispatch(ctx.channel(), frame.text().getBytes("UTF-8"));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private synchronized void resetRequestCount() {
        if (System.currentTimeMillis() - lastCountTime > 1000) {
            requestCountMap.clear();
            lastCountTime = System.currentTimeMillis();
        }
    }

    private boolean verifyRequestCount(Channel channel) {
        if (System.currentTimeMillis() - lastCountTime <= 1000) {
            int count = requestCountMap.getOrDefault(channel, 0);
            if (count >= webConfigProperties.getMaxRequestLimitCount()) {
                channel.writeAndFlush(ResponseUtil.buildData("", new ServerException(1006), Integer.MAX_VALUE));
                return false;
            }
            requestCountMap.put(channel, count + 1);
        } else {
            resetRequestCount();
        }
        return true;
    }

}