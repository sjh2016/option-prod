package com.waben.option.data.entity.activity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_activity_user_forbid")
public class ActivityUserForbid extends BaseEntity<Long> {

	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 活动类型
	 */
	private ActivityTypeEnum activityType;
	
	public static final String USER_ID = "user_id";
	
	public static final String ACTIVITY_TYPE = "activity_type";
	
}
