package com.waben.option.common.model.dto.summary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserFissionDataDTO {

    private Long userId;

    private String mobilePhone;

    /** 直接下级人数 */
    private Integer directLevelCount;

    /** 间接下级人数 */
    private Integer inDirectLevelCount;

    /** 下级入金金额 */
    private BigDecimal directPaymentAmount;

    /** 邀请奖励 */
    private BigDecimal invitationReward;

    /** 收益提成 */
    private BigDecimal income;

}
