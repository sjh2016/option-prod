package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.PaymentWayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/payment")
public class PaymentWayController extends AbstractBaseController {

    @Resource
    private PaymentWayService paymentWayService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList() {
        return ok(paymentWayService.queryList());
    }

}
