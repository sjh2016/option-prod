package com.waben.option.common.model.dto.summary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundDataDTO {

    private Long id;

    /**
     * 时间
     */
    private String day;
    
	private Integer registerNumber;

	private Integer inviteRegister;

	private Integer inviteRealRegister;

    /**
     * 总入金
     */
    private BigDecimal totalPayAmount;

    /**
     * 总入金笔数
     */
    private Integer totalPayCount;
    
    /**
     * 总入金人数
     */
    private Integer totalPayPeopleCount;
    
    private BigDecimal allTotalPayAmount;
    private Integer allTotalPayCount;
    private Integer allTotalPayPeopleCount;

    /**
     * 资产收益
     */
    private BigDecimal assetsIncome;

    /**
     * 提成收益
     */
    private BigDecimal commissionIncome;

    /**
     * 邀请奖励
     */
    private BigDecimal invitationReward;

    /**
     * 总提现金额
     */
    private BigDecimal totalWithdrawAmount;

    /**
     * 提现人数
     */
    private Integer withdrawPeopleCount;

    /**
     * 提现条数
     */
    private Integer withdrawCount;

    /**
     * 签到奖励
     */
    private BigDecimal loginReward;

    /**
     * WhatsApp奖励
     */
    private BigDecimal shareWhatsapp;

    /**
     * FaceBook奖励
     */
    private BigDecimal shareFacebook;

    /**
     * Youtube奖励
     */
    private BigDecimal shareYoutube;

    /**
     * Twitter奖励
     */
    private BigDecimal shareTwitter;

    /**
     * TG晒单
     */
    private BigDecimal tgSunshine;

    /**
     * TG加群
     */
    private BigDecimal joinGroup;

    /**
     * 活动投注奖励
     */
    private BigDecimal activityWager;

    /**
     * 免费设备收益
     */
    private BigDecimal freeEquipmentIncome;

    /** 抽奖人数 */
    private Integer lotteryUserCount;

    /** 抽奖次数 */
    private Integer lotteryCount;

    /** 抽奖消耗 */
    private BigDecimal lotteryConsume;

    /** 抽奖产出 */
    private BigDecimal lotteryOutput;
}
