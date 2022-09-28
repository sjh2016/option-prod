package com.waben.option.controller.code;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.util.AesEncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.mode.request.ClientSmsSendRequest;
import com.waben.option.service.code.CodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = {"验证码"})
@Validated
@RestController
@RequestMapping("/code")
public class CodeController extends AbstractBaseController {

    @Resource
    private CodeService smsService;

    @ApiOperation(value = "获取图形验证码")
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<?> imageCodeGenerate() {
        return ok(smsService.generateImageCode());
    }

    @ApiOperation(value = "校验图形验证码")
    @RequestMapping(value = "/image/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verifyImageCode(String sessionId, String code) {
        return ok(smsService.verifyImageCode(sessionId, code));
    }

    @ApiOperation(value = "校验图形验证码")
    @RequestMapping(value = "/image/fake/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verifyNotDeleteImageCode(String sessionId, String code) {
        return ok(smsService.verifyNotDeleteImageCode(sessionId, code));
    }

//    @ApiOperation(value = "发送短信/邮件")
//    @RequestMapping(value = "/sms/send", method = RequestMethod.GET)
//    public ResponseEntity<?> send(@RequestParam(value = "areaCode", required = false) String areaCode, @RequestParam("username") String username,
//                                  @RequestParam(value = "type", required = false) EmailTypeEnum type, @RequestParam(value = "content", required = false) String content) {
//        smsService.send(areaCode, username, type, content, getUserIp());
//        return ok();
//    }
    
    @ApiOperation(value = "发送短信/邮件")
	@Validated
	@RequestMapping(value = "/sms/send/encrypt", method = RequestMethod.POST)
	public ResponseEntity<?> sendEncrypt(@RequestBody String encryptJson) {
    	// 解密数据
		String json = null;
		try {
			if (encryptJson != null) {
				encryptJson = encryptJson.trim();
			}
			json = AesEncryptUtil.decrypt(encryptJson);
		} catch (Exception ex) {
			log.error("sms send decrypt failed:" + encryptJson);
			throw new ServerException(BusinessErrorConstants.ERROR_PARAM_FORMAT);
		}
		// 执行业务
		ClientSmsSendRequest request = JacksonUtil.decode(json, ClientSmsSendRequest.class);
		smsService.send(request.getAreaCode(), request.getUsername(), request.getType(), null, getUserIp());
        return ok();
	}

    @ApiOperation(value = "校验短信/邮箱验证码")
    @RequestMapping(value = "/sms/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(String mobilePhone, String code) {
        smsService.verify(mobilePhone, code);
        return ok();
    }

    @ApiOperation(value = "查询验证码")
    @RequestMapping(value = "/sms/queryCode", method = RequestMethod.GET)
    public ResponseEntity<?> queryCode(String mobilePhone) {
        return ok(smsService.queryCode(1L, mobilePhone));
    }

    @ApiOperation(value = "滑块验证")
    @RequestMapping(value = "/credential", method = RequestMethod.GET)
    public ResponseEntity<?> verifyCredential(@RequestParam("ticket") String ticket, @RequestParam("randStr") String randStr) {
        return ok(smsService.verifyCredential(ticket, randStr, getUserIp()));
    }

    /**
     * @param @return 参数说明
     * @return BaseRestResult 返回类型
     * @Description: 生成滑块拼图验证码
     */
    @ApiOperation(value = "生成滑块拼图验证码")
    @RequestMapping(value = "/getImageVerifyCode", method = RequestMethod.GET)
    public ResponseEntity<?> getImageVerifyCode(@RequestParam(value = "url", required = false) String url) {
        return ok(smsService.createSliderImage(url));
    }


    /**
     * 校验滑块拼图验证码
     *
     * @param moveLength 移动距离
     * @return BaseRestResult 返回类型
     * @Description: 生成图形验证码
     */
    @ApiOperation(value = "校验滑块拼图验证码")
    @RequestMapping(value = "/verifyImageCode", method = RequestMethod.GET)
    public ResponseEntity<?> verifyImageCode(@RequestParam(value = "moveLength") String moveLength, @RequestParam(value = "xWidth") Integer xWidth) {
        smsService.verifyImageCode(moveLength, xWidth);
        return ok();
    }

}
