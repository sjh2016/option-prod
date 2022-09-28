package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_d_invite")
public class Invite extends BaseTemplateEntity {

    private int numberInvite;

    private int rewardInvite;

    private int bettingInvite;

    public static final String NUMBER_INVITE = "number_invite";

}
