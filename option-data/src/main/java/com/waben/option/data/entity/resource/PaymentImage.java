package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_payment_image", autoResultMap = true)
public class PaymentImage extends BaseEntity<Long> {

    private LocalDate day;
    private String images;
    private SunshineTypeEnum type;
    private String title;

    public static final String DAY = "day";
    public static final String TYPE = "type";

}
