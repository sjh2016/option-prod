package com.waben.option.core.controller.group.email;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.group.GroupEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/group/sms")
public class GroupEmailController extends AbstractBaseController {

    @Resource
    private GroupEmailService groupEmailService;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public ResponseEntity<?> send(String strDate) {
        groupEmailService.send(strDate);
        return ok();
    }
}
