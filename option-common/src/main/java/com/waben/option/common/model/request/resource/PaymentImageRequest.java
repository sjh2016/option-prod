package com.waben.option.common.model.request.resource;

import com.waben.option.common.model.enums.SunshineTypeEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentImageRequest {

    private Long id;
    private LocalDate day;
    private String images;
    private SunshineTypeEnum type;
    private String title;

}
