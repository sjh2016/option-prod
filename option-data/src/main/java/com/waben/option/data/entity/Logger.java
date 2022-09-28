package com.waben.option.data.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.data.handler.MapObjectHandler;
import lombok.Data;

import java.util.Map;

@Data
public class Logger extends BaseEntity<Long> {

    private String cmd;

    private String cmdName;

    private String detail;

    @TableField(typeHandler = MapObjectHandler.class)
    private Map<String, Object> params;

    private String ip;

}
