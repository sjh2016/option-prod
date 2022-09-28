package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.ImageCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/code")
public class ImageCodeController extends AbstractBaseController {

    @Resource
    private ImageCodeService imageCodeService;

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<?> generate(int n) {
        return ok(imageCodeService.generateCode(n));
    }

    @RequestMapping(value = "/image/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(String sessionId, String code) {
        return ok(imageCodeService.verifyCode(sessionId, code));
    }

    @RequestMapping(value = "/image/not/delete/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verifyNotDeleteCode(String sessionId, String code) {
        return ok(imageCodeService.verifyNotDeleteCode(sessionId, code));
    }

}
