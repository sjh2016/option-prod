package com.waben.option.common.web.socket.listener;

import io.netty.channel.Channel;

public interface ConnectListener {

	void onConnect(Channel channel);
	
	void onDisconnect(Channel channel);
	
}
