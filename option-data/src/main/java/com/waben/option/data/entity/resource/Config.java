package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_d_config")
public class Config extends BaseTemplateEntity {

    @TableField(value = "`key`")
    private String key;

    private String value;

    private String remarks;

    public static final String KEY = "`key`";

}
