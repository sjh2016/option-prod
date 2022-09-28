package com.waben.option.data.entity.user;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_user_sta")
public class UserSta extends BaseEntity<Long> {

	/**
	 * 账号
	 */
	private String username;
	/**
	 * 用户uid
	 */
	private String uid;
	/**
	 * 上级ID
	 */
	private Long parentId;
	/**
	 * 累计充值金额
	 */
	private BigDecimal totalRechargeAmount;
	/**
	 * 累计提现金额
	 */
	private BigDecimal totalWithdrawAmount;
	/**
	 * 累计邀请用户数
	 */
	private Integer inviteCount;
	/**
	 * 累计邀请真实用户数
	 */
	private Integer inviteRealCount;
	/**
	 * 累计邀请充值用户数
	 */
	private Integer inviteRechargeCount;
	/**
	 * 是否为拉黑用户
	 */
	private Boolean isBlack;
	/**
	 * 是否为生成用户
	 */
	private Boolean isGenerate;
	/**
	 * 是否真实用户
	 * <p>
	 * 充值过，或者注册24小时内登陆过且分享成功过
	 * </p>
	 */
	private Boolean isReal;
	/**
	 * 注册24小时内是否登录过（不包括注册的首次登录）
	 */
	private Boolean hasFirstLogin;
	/**
	 * 注册24小时内是否成功分享过
	 */
	private Boolean hasFirstShare;

}
