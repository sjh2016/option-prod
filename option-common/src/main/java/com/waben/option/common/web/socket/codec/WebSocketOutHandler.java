package com.waben.option.common.web.socket.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.util.GZipUtil;
import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class WebSocketOutHandler extends ChannelOutboundHandlerAdapter {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private WebConfigProperties webConfigProperties;

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(!ctx.channel().isActive()) {
			ReferenceCountUtil.release(msg);
			return;
		}
		if(msg instanceof DefaultFullHttpResponse) {
			super.write(ctx, msg, promise);
			return;
		}
		Object responseData;
		if(webConfigProperties.getWebsocket().isResponseDataCompress()) {
			responseData = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(GZipUtil.compress(objectMapper.writeValueAsString(msg))));
		} else {
			responseData = new TextWebSocketFrame(Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(msg)));
		}
		super.write(ctx, responseData, promise);
	}
	
}
