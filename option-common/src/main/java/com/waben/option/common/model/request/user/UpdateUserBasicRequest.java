package com.waben.option.common.model.request.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserBasicRequest {

    private Long userId;

    /**
     * 头像地址
     */
    private String headImg;

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

    private String cpfCode;

    /**
     * 法人税号
     */
    private String cnpj;

    /**
     * pix 特别长的一个号码
     */
    private String evp;

}
