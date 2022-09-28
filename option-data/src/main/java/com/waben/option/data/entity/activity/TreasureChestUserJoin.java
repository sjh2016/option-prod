package com.waben.option.data.entity.activity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_u_treasure_chest_user_join", autoResultMap = true)
public class TreasureChestUserJoin extends BaseEntity<Long> {

	/** 用户ID */
	private Long userId;
	/** 用户UID */
	private String uid;
	/** 金额 */
	private BigDecimal amount;
	/** 日期 */
	private String day;

	public static final String USER_ID = "user_id";
	public static final String DAY = "day";

}
