package com.waben.option.common.model.dto.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageCodeDTO {

    @ApiModelProperty("图形码唯一标识")
    private String sessionId;

    @ApiModelProperty("图形码base64编码")
    private String image;

}
