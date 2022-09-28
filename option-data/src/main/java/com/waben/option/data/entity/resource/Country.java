package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_d_country", autoResultMap = true)
public class Country extends BaseTemplateEntity {

    private String countryZh;

    private String countryEn;

    private String countryCode;

    private String countryPhoneCode;

    public static final String COUNTRY_ZH = "country_zh";

    public static final String COUNTRY_EN = "country_en";

}
