package com.waben.option.common.model.request.resource;

import com.waben.option.common.model.dto.resource.BannerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateBannerRequest {

    @ApiModelProperty(value = "操作员id")
    private Long operatorId;

    @ApiModelProperty(value = "显示位置, 1:登录/注册")
    private Integer displayType;

    @ApiModelProperty(value = "轮播顺序")
    private Integer seq;

    @ApiModelProperty(value = "是否可用")
    private Boolean enable;

    @ApiModelProperty(value = "多语言LIST")
    private List<BannerDTO.BannerLanguageDTO> languageList;
}
