package com.waben.option.common.model.dto.user;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;

@Data
public class UserInvitePeopleDTO {
    private Long id;

    private String uid;

    /**
     * 账号
     */
    private String username;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 手机区号
     */
    private String areaCode;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 姓氏
     */
    private String surname;

    /**
     * 名称
     */
    private String name;

    /**
     * 层级代码
     */
    private String symbolCode;

    /**
     * 上级 层级代码
     */
    private String parentSymbolCode;

    /**
     * 下级代理用户数
     */
    private int symbolCount;

    /**
     * 子节点
     */
    private ArrayList<UserInvitePeopleDTO> childUserDTOList;

    /** 请求到账币种数量 */
    private BigDecimal reqNumSum;
    /** 请求支付币种数量 */
    private BigDecimal reqMoneySum;
    /** 实际支付币种数量 */
    private BigDecimal realMoneySum;
    /** 实际到账币种数量（扣着手续费后） */
    private BigDecimal realNumSum;

    private BigDecimal withdrawTotalAmount;

    private Long parentId;

}
