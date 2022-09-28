package com.waben.option.core.controller.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.payment.PaymentApiConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/payment_api_config")
public class PaymentApiConfigController extends AbstractBaseController {

	@Resource
	private PaymentApiConfigService service;

	/**
	 * 根据id查询通道信息
	 */
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(Long id) {
		return ok(service.query(id));
	}

	/**
	 * 查询通道信息列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list(@RequestParam(value = "cashType") List<PaymentCashType> cashType) {
		return ok(service.list(cashType));
	}

}
