package com.waben.option.common.web.socket.codec;

import com.waben.option.common.util.RequestUtil;
import com.waben.option.common.web.socket.ChannelCache;
import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;


@Slf4j
@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private ChannelCache channelCache;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String ip = request.headers().get("X-Real-IP");
            if (ip != null) {
                log.warn("websocket nginx proxy client ip: {}_{}", ip, ctx.channel().id());
            } else {
                ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
                log.warn("websocket client ip: {}", ip);
            }
            channelCache.addIp(ctx.channel(), ip);
            Map<String, String> map = RequestUtil.getRequestParams(ctx, request);
            if (map != null && map.get("userId") != null) {
                channelCache.addUserId(ctx.channel(), Long.valueOf(map.get("userId")));
            }
        }
        ctx.fireChannelRead(msg);
    }
}