package com.waben.option.common.model.dto.user;

import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserInviteTreeDTO {

    private Long id;

    private String username;

    private String mobilePhone;

    private String email;

    private String nickname;

    private String symbol;

    private String symbolCode;

    private Long parentId;

    private Integer groupIndex;

    private BigDecimal totalContribution;

    private BigDecimal yesterdayContribution;

    private InviteAuditStatusEnum inviteAuditStatus;

}
