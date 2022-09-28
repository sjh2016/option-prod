package com.waben.option.common.model.dto.push;

import lombok.*;

import java.util.List;

/**
 * <pre>
 * 推送数据
 * 1.如果 topic == null && userIdList == null 那么该数据将推送给所有连接
 * 2.如果 userIdList.size() > 0 那么该数据将忽视topic，推送给指定的用户连接
 * 3.如果 topic != null && userIdList == null 那么该数据推送给订阅所有该topic的所有连接
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushDataDTO {

    /**
     * 主题
     */
    private String topic;

    /**
     * 指令
     */
    private String cmd;

    /**
     * 用户id列表
     */
    private List<Long> userIdList;

    /**
     * 推送数据
     */
    private Object data;

}
