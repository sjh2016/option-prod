package com.waben.option.controller.payment;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.resource.AdminConfigAPI;
import com.waben.option.common.interfacesadmin.user.AdminPaymentApiConfigAPI;
import com.waben.option.common.interfacesadmin.user.AdminPaymentPassagewayAPI;
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
    private AdminPaymentPassagewayAPI adminPaymentPassagewayAPI;

    @Resource
    private AdminPaymentApiConfigAPI adminPaymentApiConfigAPI;

    @Resource
    private AdminConfigAPI adminConfigAPI;

    @RequestMapping(value = "/queryDisplayList", method = RequestMethod.GET)
    public ResponseEntity<Response<List<PaymentPassagewayDTO>>> queryDisplayList(String cashTypes) {
        return ok(adminPaymentPassagewayAPI.queryDisplayList(PaymentCashType.valueOf(cashTypes)));
    }

    @RequestMapping(value = "/queryRechargeConfig", method = RequestMethod.GET)
    public ResponseEntity<?> queryRechargeConfig() {
        return ok(adminConfigAPI.queryRechargeConfig());
    }

    @RequestMapping(value = "/queryApiId", method = RequestMethod.GET)
    public ResponseEntity<?> queryId(Long id) {
        return ok(adminPaymentApiConfigAPI.query(id));
    }

}
