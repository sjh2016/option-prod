package com.waben.option.mode.vo;

import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.util.TimeUtil;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentImageVO {

    private Long id;
    private LocalDate day;
    private String images;
    private SunshineTypeEnum type;
    private String title;

    public String getDay() {
        return TimeUtil.LocaDateToDate(day);
    }
}
