package com.waben.option.mode.query;

import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClientUserPageQuery {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "用户ID集合")
    private List<Long> idList;

    @ApiModelProperty(value = "注册开始时间")
    private Long registerStart;

    @ApiModelProperty(value = "注册结束时间")
    private Long registerEnd;

    @ApiModelProperty(value = "最后登录开始时间")
    private Long lastLoginStart;

    @ApiModelProperty(value = "最后登录结束时间")
    private Long lastLoginEnd;

    @ApiModelProperty(value = "注册类型")
    private RegisterEnum registerType;

    @ApiModelProperty(value = "用户身份类型")
    private AuthorityEnum authorityType;

    @ApiModelProperty(value = "账号来源")
    private Integer source;

    @ApiModelProperty(value = "页码")
    private int page;

    @ApiModelProperty(value = "条数")
    private int size;
}
