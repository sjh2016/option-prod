package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: Peter
 * @date: 2021/7/16 3:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_sunshine", autoResultMap = true)
public class Sunshine extends BaseEntity<Long> {

    private Long userId;

    private String username;

    private SunshineTypeEnum type;

    private String url;
    
	/** 上传图片数量 */
	private Integer urlSize;
	/** 已上传图片数量 */
	private Integer currentUrlSize;

    private String localDate;

    private Boolean enable;

    public static final String USER_ID = "user_id";

    public static final String TYPE = "type";

    public static final String ENABLE = "enable";
    
}
