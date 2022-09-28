package com.waben.option.controller.payment;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUserPageRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.payment.PaymentOrderService;

import io.swagger.annotations.Api;

/**
 * 充值订单接口
 */
@Api(tags = { "充值订单" })
@RestController
@RequestMapping("/payment_order")
public class PaymentOrderController extends AbstractBaseController {

	@Resource
	private PaymentOrderService service;

	@Resource
	private RedisTemplate<Serializable, Object> redisTemplate;

	@RequestMapping(value = "/place_order", method = RequestMethod.POST)
	public ResponseEntity<?> placeOrder(@RequestBody PayFrontRequest request) {
		String platform = getPlatform();
		if (!StringUtils.isBlank(platform)) {
			redisTemplate.opsForHash().put(RedisKey.OPTION_USER_PAYMENT_PLATFORM_KEY, getCurrentUserId(), platform);
		}
		return ok(service.pay(getCurrentUserId(), getUserIp(), request));
	}

	@RequestMapping(value = "/user/page", method = RequestMethod.GET)
	public ResponseEntity<?> userPage(PaymentUserPageRequest req) {
		return ok(service.userPage(getCurrentUserId(), req));
	}

//	@RequestMapping(value = "admin/page", method = RequestMethod.GET)
//	public ResponseEntity<?> adminPage(PaymentAdminPageRequest req) {
//		if (getCurrentUserId().equals(1420302342074400766L)) {
//			req.setIsAll(true);
//		} else {
//			req.setIsAll(false);
//		}
//		return ok(service.adminPage(req));
//	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ResponseEntity<?> detail(@RequestParam(value = "id", required = true) Long id) {
		return ok(service.detail(id));
	}

	@RequestMapping(value = "/last", method = RequestMethod.GET)
	public ResponseEntity<?> last() {
		return ok(service.last(getCurrentUserId()));
	}

}
