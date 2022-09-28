package com.waben.option.common.web.socket.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.web.socket.WebSocketServer;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Sharable
@ConditionalOnBean(WebSocketServer.class)
public class JsonEncoder extends MessageToMessageEncoder<Object>{

	@Resource
	private ObjectMapper objectMapper;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception {
		if (object == null) {
            return;
        }
		out.add(objectMapper.writeValueAsString(object));
	}
	
}
