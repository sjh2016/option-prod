package com.waben.option.common.model.dto.summary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundTotalDTO {

    /** 提成收益 */
    private BigDecimal commissionIncome;

    /** 邀请奖励 */
    private BigDecimal invitationReward;

    /** 签到奖励 */
    private BigDecimal loginReward;

    /** WhatsApp奖励 */
    private BigDecimal shareWhatsapp;

    /** FaceBook奖励 */
    private BigDecimal shareFacebook;

    /** Youtube奖励 */
    private BigDecimal shareYoutube;

    /** Twitter奖励 */
    private BigDecimal shareTwitter;

    /** TG晒单 */
    private BigDecimal tgSunshine;

    /** TG加群 */
    private BigDecimal joinGroup;

    /** 活动投注奖励 */
    private BigDecimal activityWager;

}
