package com.waben.option.mode.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BannerVO {

    @ApiModelProperty(value = "轮播图id")
    private Long id;

    @ApiModelProperty(value = "操作员id")
    private Long operatorId;

    @ApiModelProperty(value = "显示位置, 1:登录/注册")
    private Integer displayType;

    @ApiModelProperty(value = "轮播顺序")
    private Integer seq;

    @ApiModelProperty(value = "是否可用")
    private Boolean enable;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "轮播图描述")
    private String description;

    @ApiModelProperty(value = "黑色图片地址")
    private String imgDarkUrl;

    @ApiModelProperty(value = "白色图片地址")
    private String imgLightUrl;

    @ApiModelProperty(value = "跳转地址")
    private String skipUrl;
}
