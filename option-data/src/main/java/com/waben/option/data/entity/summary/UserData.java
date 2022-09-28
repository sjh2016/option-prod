package com.waben.option.data.entity.summary;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_user_data")
public class UserData extends BaseEntity<Long> {

    /**
     * 日期
     */
    private String day;

    /**
     * 注册人数
     */
    private int registerNumber;

    /**
     * 入金金额
     */
    private BigDecimal paymentAmount;

    /**
     * 入金人数
     */
    private Integer paymentUserCount;

    /**
     * 被邀请人数
     */
    private int beInvites;

    /**
     * 被邀请人入金
     */
    private BigDecimal beInvitesPaymentAmount;

    public static final String DAY = "day";

    public static final String GMT_CREATE = "gmt_create";

}
