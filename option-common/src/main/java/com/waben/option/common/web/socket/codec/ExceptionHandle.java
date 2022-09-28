package com.waben.option.common.web.socket.codec;

import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class ExceptionHandle extends ChannelInboundHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.warn("channel id: {} has exception to close, exception msg: {}", ctx.channel().id().asShortText(),
				cause.getMessage());
		log.error("", cause);
		ctx.close();
	}

}
