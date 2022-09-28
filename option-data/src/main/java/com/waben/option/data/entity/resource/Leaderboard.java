package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.LeaderboardTypeEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 11:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_leader_board", autoResultMap = true)
public class Leaderboard extends BaseEntity<Long> {

    private String name;

    private String headImg;

    private LeaderboardTypeEnum type;

    private BigDecimal power;

    private Integer workOrderCount;

    public static final String POWER = "power";
    public static final String TYPE = "type";

}
