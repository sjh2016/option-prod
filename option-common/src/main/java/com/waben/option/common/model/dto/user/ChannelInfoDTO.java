package com.waben.option.common.model.dto.user;

import lombok.Data;

@Data
public class ChannelInfoDTO {

    private Long userId;

    private String channelId;

    private String host;

    private int port;

    public String getHostPost() {
        return host + ":" + port;
    }

}
