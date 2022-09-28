package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/7/16 3:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_mission_activity", autoResultMap = true)
public class MissionActivity extends BaseEntity<Long> {

    private String name;

    private ActivityTypeEnum type;

    private BigDecimal amount;

    private Integer minLimitNumber;

    private Integer maxLimitNumber;

    private String description;

    private Integer sort;

    private Boolean enable;

    private Boolean daily;

    private Boolean awardCreate;

    private Boolean limitInviteVolume;
    
    private BigDecimal stepInvestment;

    public static final String SORT = "sort";
    public static final String TYPE = "type";
    public static final String ENABLE = "enable";


}
