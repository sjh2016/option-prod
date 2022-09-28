package com.waben.option.controller.payment;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentApiConfigAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentPassagewayAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

/**
 * 支付通道信息接口
 */
@Api(tags = {"支付通道"})
@RestController
@RequestMapping("/payment_passageway")
public class PaymentPassagewayController extends AbstractBaseController {

    @Resource
    private PaymentPassagewayAPI service;

    @Resource
    private PaymentApiConfigAPI paymentApiConfigAPI;

    @Resource
    private ConfigAPI config;

    @RequestMapping(value = "/queryDisplayList", method = RequestMethod.GET)
    public ResponseEntity<Response<List<PaymentPassagewayDTO>>> queryDisplayList(String cashTypes) {
        return ok(service.queryDisplayList(PaymentCashType.valueOf(cashTypes)));
    }

    @RequestMapping(value = "/queryRechargeConfig", method = RequestMethod.GET)
    public ResponseEntity<?> queryRechargeConfig() {
        return ok(config.queryRechargeConfig());
    }

//    @RequestMapping(value = "/queryApiId", method = RequestMethod.GET)
//    public ResponseEntity<?> queryId(Long id) {
//        return ok(paymentApiConfigAPI.query(id));
//    }

}
