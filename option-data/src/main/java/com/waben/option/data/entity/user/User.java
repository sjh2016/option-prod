package com.waben.option.data.entity.user;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_user")
public class User extends BaseEntity<Long> {

    private String uid;

    /**
     * 头像地址
     */
    private String headImg;

    /**
     * 账号
     */
    private String username;

    /**
     * 登陆密码
     */
    private String loginPassword;

    /**
     * 支付密码
     */
    private String fundPassword;

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
     * 注册终端
     */
    private PlatformEnum registerPlatform;

    /**
     * 账号来源
     * <p>1：用户注册</p>
     * <p>2：系统生成</p>
     */
    private Integer source = 1;

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
	 * 上级ID（真实）
	 */
	private Long realParentId;

    /**
     * 身份证（社保号）
     */
    private String cpfCode;

    /**
     * 等级
     */
    private Integer level;
    
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
     * 用户组
     */
    private Integer groupIndex;
    
    /**
     * 星级
     */
    private Integer starLevel;
    /**
     * 是否为vip用户
     */
    private Boolean isVip;
    /**
     * 是否为拉黑用户
     */
    private Boolean isBlack;
    /**
	 * 是否真实用户
	 * <p>
	 * 充值过，或者注册24小时内登陆过且分享成功过
	 * </p>
	 */
	private Boolean isReal;
	/**
	 * 是否测试用户
	 */
	private Boolean isTest;

	private String topId;

    public static final String UID = "uid";

    public static final String USERNAME = "username";

    public static final String LOGIN_PASSWORD = "login_password";

    public static final String NAME = "name";

    public static final String REGISTER_IP = "register_ip";

    public static final String LAST_LOGIN_IP = "last_login_ip";

    public static final String LAST_LOGIN_TIME = "last_login_time";

    public static final String LAST_LOGOUT_TIME = "last_logout_time";

    public static final String SOURCE = "source";

    public static final String REGISTER_TYPE = "register_type";

    public static final String AUTHORITY_TYPE = "authority_type";

    public static final String SYMBOL = "symbol";

    public static final String SYMBOL_CODE = "symbol_code";

    public static final String PARENT_ID = "parent_id";

    public static final String MOBILE_PHONE = "mobile_phone";
    
    public static final String GROUP_INDEX = "group_index";
    
    public static final String IS_BLACK = "is_black";
    
    public static final String IS_REAL = "is_real";

    public static final String PARENT_SYMBOL_CODE = "parent_symbol_code";

    public static final String TOP_ID = "top_id";
}
