package com.waben.option.common.web.socket.codec;

import com.waben.option.common.util.GZipUtil;
import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class GZipEncoder extends MessageToMessageEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
		if (message == null) {
            return;
        }
        out.add(Unpooled.wrappedBuffer(GZipUtil.compress(message)));
	}
	
}
