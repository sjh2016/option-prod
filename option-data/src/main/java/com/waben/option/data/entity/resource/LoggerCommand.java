package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;
import com.waben.option.data.handler.ListStringTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_d_logger_command", autoResultMap = true)
public class LoggerCommand extends BaseTemplateEntity {

    private String cmd;

    private String name;

    private String detail;

    private String platform;

    @TableField(value = "`keys`", typeHandler = ListStringTypeHandler.class)
    private List<String> keyList;

    public static final String CMD = "cmd";

    public static final String PLATFORM = "platform";

}