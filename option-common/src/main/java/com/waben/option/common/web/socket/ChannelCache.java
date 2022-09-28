package com.waben.option.common.web.socket;

import com.waben.option.common.component.ChannelContext;
import com.waben.option.common.util.IpUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelCache {

    private final Set<Channel> channelSet = new HashSet<>();

    private final Map<Channel, String> ipAddressMap = new ConcurrentHashMap<>();

    private final Map<String, Channel> channelIdMap = new ConcurrentHashMap<>();

    private final Map<Channel, String> tokenMap = new ConcurrentHashMap<>();

    private final Map<Channel, Long> channelUserIdMap = new ConcurrentHashMap<>();

    private final Map<Long, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    private Map<Channel, InetSocketAddress> channelRealAddressMap = new HashMap<>();

    public synchronized void addChannel(Channel channel) {
        channelSet.add(channel);
        channelIdMap.put(channel.id().asLongText(), channel);
    }

    /**
     * 获取当前服务器所有活跃channel
     * 使用该方法做遍历的时候，一定要重新new一个新的Collection来进行操作，否则容易引起集合并发冲突
     *
     * @return
     */
    public Set<Channel> getAllChannels() {
        return channelSet;
    }

    public synchronized void removeChannel(Channel channel) {
        channelSet.remove(channel);
        channelIdMap.remove(channel.id().asShortText());
        ipAddressMap.remove(channel);
        tokenMap.remove(channel);
        Long userId = channelUserIdMap.remove(channel);
        if (userId != null) {
            userIdChannelMap.remove(userId);
        } else {
            List<Long> keyList = new ArrayList<>(userIdChannelMap.keySet());
            for (Long tmpUserId : keyList) {
                Channel tmpChannel = userIdChannelMap.get(tmpUserId);
                if (tmpChannel == null) {
                    userIdChannelMap.remove(tmpUserId);
                    break;
                }
            }
        }
        channel.close();
    }

    public String getIp() {
        return getIp(ChannelContext.getCurrentChannel());
    }

    public String getIp(Channel channel) {
        String address = null;
        if (channel == null) {
            address = IpUtil.getIp();
        } else {
            address = ipAddressMap.get(channel);
            if (address == null) {
                return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
            }
        }
        return address;
    }

    public String getToken(Channel channel) {
        return tokenMap.get(channel);
    }

    public Channel getChannel(String channelId) {
        return channelIdMap.get(channelId);
    }

    public void addIp(Channel channel, String ip) {
        ipAddressMap.put(channel, ip);
    }

    public void addToken(Channel channel, String token) {
        tokenMap.put(channel, token);
    }

    public void addUserId(Channel channel, Long userId) {
        userIdChannelMap.put(userId, channel);
        channelUserIdMap.put(channel, userId);
    }

    public InetSocketAddress getChannelRealAddress(Channel channel) {
        InetSocketAddress address = channelRealAddressMap.get(channel);
        if (address == null) {
            address = (InetSocketAddress) channel.remoteAddress();
        }
        return address;
    }

    public Long getUserId(Channel channel) {
        if (channelUserIdMap != null) {
            return channelUserIdMap.get(channel);
        }
        return null;
    }
}
