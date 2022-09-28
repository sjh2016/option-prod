package com.waben.option.common.model.dto.push;

import lombok.Data;

import java.util.List;

@Data
public class PushChannelDTO {

    private String cmd;

    private List<String> channelIdList;

    private Object data;

}
