package com.waben.option.thirdparty.controller;

import com.waben.option.common.component.SpringContext;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import com.waben.option.thirdparty.service.sms.SendBeanService;
import com.waben.option.thirdparty.service.sms.huaweiyun.HuaWeiSmsService;
import com.waben.option.thirdparty.service.tencent.DescribeCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RefreshScope
@RestController
@RequestMapping("/sms")
public class SmsController extends AbstractBaseController {

	@Resource
	private HuaWeiSmsService smsService;

	@Resource
	private SendBeanService sendBeanService;

	@Resource
	private DescribeCaptchaService describeCaptchaService;

	@Value("${emailBeanName}")
	private String emailBeanName;

	@Value("${smsBeanName}")
	private String smsBeanName;

	@Value("${onbukaBeanName}")
	private String onbukaBeanName;

	/**
	 * 滑块验证码
	 *
	 * @return
	 */
	@RequestMapping(value = "/verifyCredential", method = RequestMethod.GET)
	public ResponseEntity<?> verifyCredential(String ticket, String randStr, String userIp) {
		return ok(describeCaptchaService.verifyCredential(ticket, randStr, userIp));
	}

	/**
	 * 发送验证码
	 *
	 * @param areaCode 区号
	 * @param username 账号
	 * @param code     验证码
	 * @return
	 */
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ResponseEntity<?> sendCode(String areaCode, String username, String code, EmailTypeEnum type, String content,
			String ip) {


		if (type !=null && "ONBUKA".equalsIgnoreCase(type.name())){
			log.info("-->in onbuka:{}",onbukaBeanName);
			AbstractBaseSmsService onbukaService = SpringContext.getBean(onbukaBeanName, AbstractBaseSmsService.class);
			onbukaService.sendCode(areaCode, username, code, content, ip);
			log.info("-->in onbukaService:");
			return ok();
		}


		if (type != null) {
			AbstractBaseSmsService emailService = SpringContext.getBean(emailBeanName, AbstractBaseSmsService.class);
			emailService.sendCode(username, code, type, content, ip);

			// SpringContext.getBean(AmazonEmailService.class).sendCode(username, code, type, content, ip);
			// SpringContext.getBean(TencentCloudEmailService.class).sendCode(username, code, type, content, ip);
		} else {
			AbstractBaseSmsService smsService = SpringContext.getBean(smsBeanName, AbstractBaseSmsService.class);
			smsService.sendCode(areaCode, username, code, content, ip);

			// SpringContext.getBean(SmgwSmsService.class).sendCode(areaCode, username, code, content, ip);
			// SpringContext.getBean(GlobalSmsService.class).sendCode(areaCode, username, code, content, ip);
			// SpringContext.getBean(Sms230SmsService.class).sendCode(areaCode, username, code, content, ip);
		}
		return ok();
	}

	@RequestMapping(value = "/send/onbuka", method = RequestMethod.GET)
	public ResponseEntity<?> sendCodeV2(String areaCode, String username, String code, EmailTypeEnum type, String content,
									  String ip) {
		AbstractBaseSmsService onbukaService = SpringContext.getBean(onbukaBeanName, AbstractBaseSmsService.class);
		onbukaService.sendCode(areaCode, username, code, content, ip);
		return ok();
	}

	/**
	 * 校验验证码
	 *
	 * @param username 账号
	 * @param code     验证码
	 * @return
	 */
	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	public ResponseEntity<?> verifyCode(String username, String code) {
		if (username.startsWith("0"))
			username = username.substring(1);
		return ok(smsService.verifyCode(username, code));
	}

	/**
	 * 删除验证码
	 *
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCode(String username) {
		if (username.startsWith("0"))
			username = username.substring(1);
		smsService.deleteCode(username);
		return ok();
	}

	@RequestMapping(value = "/queryCode", method = RequestMethod.GET)
	public ResponseEntity<?> queryCode(@RequestParam(value = "currentUserId", required = false) Long currentUserId,
			@RequestParam(value = "username") String username) {
		if (username.startsWith("0"))
			username = username.substring(1);
		return ok(smsService.queryCode(currentUserId, username));
	}

}
