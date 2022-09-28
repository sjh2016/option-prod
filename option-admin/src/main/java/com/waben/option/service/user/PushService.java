package com.waben.option.service.user;

import com.waben.option.common.model.dto.push.PushChannelDTO;
import com.waben.option.common.model.dto.push.PushDataDTO;
import com.waben.option.common.util.ResponseUtil;
import com.waben.option.common.web.socket.ChannelCache;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PushService {

	@Resource
	private ChannelCache channelCache;

	public void push(PushChannelDTO pushData) {
		Map<String, Object> data = ResponseUtil.buildData(pushData.getCmd(), pushData.getData());
		log.info("try push: {}", data);
		if (CollectionUtils.isEmpty(pushData.getChannelIdList())) {
			List<Channel> channelList = new ArrayList<>(channelCache.getAllChannels());
			for (Channel channel : channelList) {
				if (channel.isActive()) {
					channel.writeAndFlush(data);
				}
			}
		} else {
			for (String channelId : pushData.getChannelIdList()) {
				Channel channel = channelCache.getChannel(channelId);
				if (channel != null && channel.isActive()) {
					log.info("do push: {}|{}", channelId, data);
					channel.writeAndFlush(data);
				}
			}
		}
	}

}
