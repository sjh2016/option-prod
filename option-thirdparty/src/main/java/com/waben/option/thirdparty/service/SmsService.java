package com.waben.option.thirdparty.service;


import com.waben.option.common.model.enums.EmailTypeEnum;

public interface SmsService {

    /**
     * 发送验证码
     *
     * @param areaCode
     * @param mobilePhone
     * @param code
     * @return
     */
    public boolean sendCode(String areaCode, String mobilePhone, String code, String content, String ip);

    public boolean sendCode(String toEmail, String code, EmailTypeEnum type, String content, String ip);

    /**
     * 校验验证码
     *
     * @param mobilePhone
     * @param code
     * @return
     */
    public boolean verifyCode(String mobilePhone, String code);

    /**
     * 删除校验码
     *
     * @param mobilePhone
     * @return
     */
    public boolean deleteCode(String mobilePhone);

    /**
     * 查询验证码
     *
     * @param currentUserId
     * @param mobilePhone
     * @return
     */
    public String queryCode(Long currentUserId, String mobilePhone);

}
