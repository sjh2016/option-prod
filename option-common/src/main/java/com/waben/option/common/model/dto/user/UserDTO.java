package com.waben.option.common.model.dto.user;

import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class UserDTO {

    private Long id;

    private String uid;

    /**
     * 账号
     */
    private String username;

    /**
     * 登陆密码
     */
    private String loginPassword;

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
     * 国家
     */
    private String country;

    /**
     * 国家代码
     */
    private String countryCode;

    /**
     * 城市
     */
    private String city;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 地址
     */
    private String address;

    /**
     * 地址详情
     */
    private String addressDetails;

    /**
     * 注册类型
     */
    private RegisterEnum registerType;

    /**
     * 用户类型
     */
    private AuthorityEnum authorityType;

    /**
     * 账号来源
     * <p>1：用户注册</p>
     * <p>2：系统生成</p>
     */
    private Integer source;

    /**
     * 注册ip
     */
    private String registerIp;

    /**
     * 最后一次登陆
     */
    private String lastLoginIp;

    /**
     * 最后一次登陆时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后一次登出时间
     */
    private LocalDateTime lastLogoutTime;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 层级编码
     */
    private String symbol;

    /**
     * 层级代码
     */
    private String symbolCode;

    /**
     * 上级 层级代码
     */
    private String parentSymbolCode;

    /**
     * 上级id
     */
    private Long parentId;

    /**
     * 下级代理用户数
     */
    private int symbolCount;

    /**
     * 身份证（社保号）
     */
    private String cpfCode;

    private BigDecimal totalContribution;

    private BigDecimal yesterdayContribution;

    private String headImg;

    private Integer level;

    private String parentPhone;

    /**
     * 法人税号
     */
    private String cnpj;

    /**
     * pix 特别长的一个号码
     */
    private String evp;

    /**
     * 抽奖次数
     */
    private Integer luckyDraws;

    /**
     * 星级
     */
    private Integer starLevel;
    /**
     * 用户组
     */
    private Integer groupIndex;
    /**
     * 是否为vip用户
     */
    private Boolean isVip;
    /**
     * 是否为拉黑用户
     */
    private Boolean isBlack;

    private String topId;
    
}
