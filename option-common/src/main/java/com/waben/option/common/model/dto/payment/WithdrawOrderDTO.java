package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.WithdrawOrderStatusEnum;
import com.waben.option.common.model.enums.WithdrawTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现订单
 */
@Data
public class WithdrawOrderDTO {

	/** 主键ID */
	private Long id;
	/** 用户ID */
	private Long userId;
	private String uid;
	private String username;
	private String nickname;
	/** 代理symbol */
	private String brokerSymbol;
	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 订单编号 */
	private String orderNo;
	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 上游返回信息 */
	private String thirdRespMsg;
	/** 订单状态 */
	private WithdrawOrderStatusEnum status;
	/** 提现类型 */
	private WithdrawTypeEnum type;
	/** 请求提现数量（USDT） */
	private BigDecimal reqNum;
	/** 请求提现金额 */
	private BigDecimal reqMoney;
	/** 提现到账数量（扣除手续费） */
	private BigDecimal realNum;
	/** 提现币种 */
	private CurrencyEnum reqCurrency;
	/** 到账币种 */
	private CurrencyEnum targetCurrency;
	/** 手续费 */
	private BigDecimal fee;
	/** 支付apiID */
	private Long payApiId;
	/** 支付api名称 */
	private String payApiName;
	/** 支付方式ID */
	private Long payMethodId;
	/** 支付方式名称 */
	private String payMethodName;
	/** 支付通道ID */
	private Long passagewayId;
	/** 汇率 */
	private BigDecimal exchangeRate;
	/** 钱包地址（提币才有值） */
	private String burseAddress;
	/** 钱包类型（提币才有值），如ERC20、TRC20 */
	private String burseType;
	/** 交易hash（提币才有值） */
	private String hash;
	/** 银行卡号（OTC出售才有值） */
	private String bankCardId;
	/** 姓名（OTC出售才有值） */
	private String name;
	/** 手机号码（OTC出售才有值） */
	private String mobilePhone;
	/** 身份证号（OTC出售才有值） */
	private String idCard;
	/** 所属银行名称（OTC出售才有值） */
	private String bankName;
	/** 所属银行代码（OTC出售才有值） */
	private String bankCode;
	/** 省份名称（OTC出售才有值） */
	private String provinceName;
	/** 城市名称（OTC出售才有值） */
	private String cityName;
	/** 所属支行名称（OTC出售才有值） */
	private String branchName;
	/** 备注 */
	private String remark;
	/** 是否提现红包收益 */
	private Boolean isLuckyProfit;
	/** 到账时间 */
	private LocalDateTime arrivalTime;
	/** 审核时间 */
	private LocalDateTime auditTime;
	/** 审核人员ID */
	private Long auditUserId;
	/** 审核人用户名 */
	private String auditUsername;

	private LocalDateTime gmtCreate;

	/** 累计提现金额 */
	private BigDecimal totalWithdrawAmount = BigDecimal.ZERO;

	/** 累计入金金额 */
	private BigDecimal totalRechargeAmount = BigDecimal.ZERO;

	/** 邀请奖励 */
	private BigDecimal totalInviteAmount = BigDecimal.ZERO;

	/** 登录奖励 */
	private BigDecimal totalLoginAmount = BigDecimal.ZERO;

	/** 提成收益 */
	private BigDecimal totalDivideAmount = BigDecimal.ZERO;

	/** 邀请人数 */
	private Integer invitePeople = 0;

	/** 邀请入金人数 */
	private Integer inviteRechargePeople = 0;
}
