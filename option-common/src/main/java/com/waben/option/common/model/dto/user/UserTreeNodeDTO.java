package com.waben.option.common.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserTreeNodeDTO {

    private Long id;

    private String username;

    private String mobilePhone;

    private String email;

    private String nickname;

    private String symbol;

    private String symbolCode;

    private Long parentId;

    private List<UserTreeNodeDTO> childrenList;

    private BigDecimal totalContribution;

    private BigDecimal yesterdayContribution;

    private String headImg;

    private String parentPhone;

    private Integer level;

    public void add(UserTreeNodeDTO node) {
        childrenList.add(node);
    }

}
