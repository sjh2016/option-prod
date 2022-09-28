package com.waben.option.thirdparty.controller;

import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.thirdparty.service.sms.amazon.AmazonGroupEmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/email")
public class EmailController extends AbstractBaseController {

    @Resource
    private AmazonGroupEmailService amazonGroupEmailService;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public ResponseEntity<?> sendCode(String username, EmailTypeEnum type, String content) {
        return ok(amazonGroupEmailService.sendCode(username, null, type, content, null));
    }
}
