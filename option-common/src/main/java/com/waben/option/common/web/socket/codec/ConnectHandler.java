package com.waben.option.common.web.socket.codec;

import com.waben.option.common.web.socket.ChannelCache;
import com.waben.option.common.web.socket.WebSocketServer;
import com.waben.option.common.web.socket.listener.ConnectListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Sharable
@Component
@ConditionalOnBean(WebSocketServer.class)
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private ChannelCache channelCache;

    private ConnectListener connectListener;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channelCache.addChannel(ctx.channel());
        if (connectListener != null) {
            connectListener.onConnect(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (connectListener != null) {
            connectListener.onDisconnect(ctx.channel());
        }
        channelCache.removeChannel(ctx.channel());
    }

    private void removeCache() {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    if (ctx.channel().isActive()) {
                        ctx.close();
                        log.warn("channel {} has long time send data, close it", ctx.channel());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void registerListener(ConnectListener listener) {
        this.connectListener = listener;
    }

}
