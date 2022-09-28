package com.waben.option.common.web.socket.codec;

import com.waben.option.common.service.DispatcherMessageService;
import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class DispatcherHandler extends ChannelInboundHandlerAdapter {

	@Resource
	private DispatcherMessageService dispatcherMessageService;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf buf = (ByteBuf) msg;
		byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		buf.release();
		dispatcherMessageService.dispatch(ctx.channel(), data);
	}

}
