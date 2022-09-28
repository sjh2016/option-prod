package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: Peter
 * @date: 2021/6/2 15:39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_language", autoResultMap = true)
public class Language extends BaseEntity<Long> {

    private String code;

    private String locale;

    private String name;

    private String englishName;

    private String countryCode;

    private Boolean enable;

    private Integer sort;

    public static final String ENABLE = "enable";
    public static final String SORT = "sort";
    public static final String LOCALE = "locale";
    public static final String CODE = "code";

}
