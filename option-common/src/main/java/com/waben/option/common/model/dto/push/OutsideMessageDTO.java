package com.waben.option.common.model.dto.push;

import com.waben.option.common.model.enums.OutsidePushMessageType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OutsideMessageDTO {

    /**
     * 引用ID
     */
    private Long referenceId;
    /**
     * 类型
     */
    private OutsidePushMessageType type;
    /**
     * 用户ID列表，如果为null或者空表示广播
     */
    private List<Long> userIds;
    /**
     * 参数
     */
    private Map<String, String> params;

}
