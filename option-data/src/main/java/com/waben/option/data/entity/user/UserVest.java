package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户所属马甲
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_vest")
public class UserVest extends BaseEntity<Long> {

	/** 用户ID */
	private Long userId;
	/** 设备类型，1IOS 2安卓 */
	private Integer deviceType;
	/** 马甲包序号 */
	private Integer vestIndex;

	public static final String USER_ID = "user_id";

}
