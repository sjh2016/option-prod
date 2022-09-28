package com.waben.option.data.entity.summary;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_fund_data")
public class FundData extends BaseEntity<Long> {

	private String day;

	private Integer registerNumber;

	private Integer inviteRegister;

	private Integer inviteRealRegister;

	private BigDecimal totalPayAmount;

	private Integer totalPayCount;

	private Integer totalPayPeopleCount;

	private BigDecimal allTotalPayAmount;

	private Integer allTotalPayCount;

	private Integer allTotalPayPeopleCount;

	private BigDecimal assetsIncome;

	/**
	 * 提成收益
	 **/
	private BigDecimal commissionIncome;

	/**
	 * 邀请奖励
	 **/
	private BigDecimal invitationReward;

	private BigDecimal totalWithdrawAmount;

	private Integer withdrawCount;

	private Integer withdrawPeopleCount;

	private BigDecimal loginReward;

	private BigDecimal shareWhatsapp;

	private BigDecimal shareFacebook;

	private BigDecimal shareYoutube;

	private BigDecimal shareTwitter;

	private BigDecimal tgSunshine;

	private BigDecimal activityWager;

	private BigDecimal freeEquipmentIncome;

	private BigDecimal joinGroup;

	private Integer lotteryUserCount;

	private Integer lotteryCount;

	private BigDecimal lotteryConsume;

	private BigDecimal lotteryOutput;

	public static final String DAY = "day";

	public static final String GMT_CREATE = "gmt_create";

}
