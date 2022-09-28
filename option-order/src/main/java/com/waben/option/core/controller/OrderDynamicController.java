package com.waben.option.core.controller;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.order.OrderDynamicService;

@RestController
@RequestMapping("/order_dynamic")
public class OrderDynamicController extends AbstractBaseController {

	@Resource
	private OrderDynamicService orderDynamicService;

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	public ResponseEntity<?> page(@RequestParam("page") int page, @RequestParam("size") int size) {
		return ok(orderDynamicService.page(page, size));
	}

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public ResponseEntity<?> generate(@RequestParam("size") int size) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				orderDynamicService.generate(size);
			}
		}).start();
		return ok();
	}

}
