package com.waben.option.data.entity.activity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_d_treasure_chest", autoResultMap = true)
public class TreasureChest extends BaseTemplateEntity {

	/** 密码 */
	private String password;
	/** 最大可抽奖次数 */
	private Integer limitQuantity;
	/** 已抽奖次数 */
	private Integer usedQuantity;
	/** 中奖概率设置 */
	private String probability;
	/** 是否上线 */
	private Boolean online;
	/** 排序 */
	private Integer sort;
	/** 开始时间 */
	private LocalDateTime startTime;
	/** 结束时间 */
	private LocalDateTime endTime;
	
	public static final String ONLINE = "online";
	public static final String SORT = "sort";

}
