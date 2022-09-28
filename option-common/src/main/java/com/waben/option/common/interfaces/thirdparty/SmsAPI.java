package com.waben.option.common.interfaces.thirdparty;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.enums.EmailTypeEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "thirdparty-server", contextId = "SmsAPI", qualifier = "smsAPI")
public interface SmsAPI extends BaseAPI {

    /**
     * 滑块验证
     *
     * @return
     */
    @RequestMapping(value = "/sms/verifyCredential", method = RequestMethod.GET)
    public Response<String> _verifyCredential(@RequestParam("ticket") String ticket, @RequestParam("randStr") String randStr,
                                              @RequestParam("userIp") String userIp);

    /**
     * 发送验证码
     *
     * @param code 验证码
     * @return
     */
    @RequestMapping(value = "/sms/send", method = RequestMethod.GET)
    public Response<Boolean> _sendCode(@RequestParam("areaCode") String areaCode, @RequestParam("username") String username,
                                       @RequestParam("code") String code, @RequestParam(value = "type", required = false) EmailTypeEnum type, @RequestParam(value = "content", required = false) String content, @RequestParam("ip") String ip);


    @RequestMapping(value = "/email/send", method = RequestMethod.GET)
    public Response<Boolean> _sendEmail(@RequestParam("username") String username,
                                       @RequestParam("code") String code, @RequestParam(value = "type", required = false) EmailTypeEnum type, @RequestParam(value = "content", required = false) String content);
    /**
     * 校验验证码
     *
     * @param code 验证码
     * @return
     */
    @RequestMapping(value = "/sms/verify", method = RequestMethod.GET)
    public Response<Boolean> _verifyCode(@RequestParam("username") String username,
                                         @RequestParam("code") String code);

    /**
     * 删除验证码
     *
     * @return
     */
    @RequestMapping(value = "/sms/delete", method = RequestMethod.DELETE)
    public Response<Void> _deleteCode(@RequestParam("username") String username);

    /**
     * 查询验证码
     *
     * @param currentUserId 接口访问用户id
     * @return
     */
    @RequestMapping(value = "/sms/queryCode", method = RequestMethod.GET)
    public Response<String> _queryCode(@RequestParam(value = "currentUserId", required = false) Long currentUserId,
                                       @RequestParam("username") String username);

    public default Boolean sendCode(String areaCode, String username, String code, EmailTypeEnum type, String content, String ip) {
        return getResponseData(_sendCode(areaCode, username, code, type, content, ip));
    }

    public default Boolean sendEmail(String username, String code, EmailTypeEnum type, String content) {
        return getResponseData(_sendEmail(username, code, type, content));
    }

    public default String queryCode(Long currentUserId, String username) {
        return getResponseData(_queryCode(currentUserId, username));
    }

    public default void deleteCode(String username) {
        getResponseData(_deleteCode(username));
    }

    public default Boolean verifyCode(String username, String code) {
        return getResponseData(_verifyCode(username, code));
    }

    public default String verifyCredential(String ticket, String randStr, String userIp) {
        return getResponseData(_verifyCredential(ticket, randStr, userIp));
    }
}
