package com.waben.option.mode.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserTreeNodeVO {

    private Long id;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "层级编码")
    private String symbol;

    @ApiModelProperty(value = "层级代码")
    private String symbolCode;

    @ApiModelProperty(value = "上级id")
    private Long parentId;

    @ApiModelProperty(value = "下级用户集合")
    private List<UserTreeNodeVO> childrenList = new ArrayList<>();

    @ApiModelProperty(value = "下级邀请用户数量")
    private int symbolCount;

    @ApiModelProperty(value = "子代理数量")
    private int childrenCount;

    @ApiModelProperty(value = "总贡献")
    private BigDecimal totalContribution;

    @ApiModelProperty(value = "昨日贡献")
    private BigDecimal yesterdayContribution;

    @ApiModelProperty(value = "头像地址")
    private String headImg;

    @ApiModelProperty(value = "上级名称")
    private String parentPhone;

}
