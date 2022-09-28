package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: Peter
 * @date: 2021/7/8 13:54
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_caller_links")
public class CallerLinks extends BaseEntity<Long> {

    private String name;

    private String link;

    private String type;

    private Boolean enable;

    public static final String NAME = "name";
    public static final String LINK = "link";
    public static final String TYPE = "type";
    public static final String ENABLE = "enable";
}
