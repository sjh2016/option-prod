package com.waben.option.core.controller.resource;

import com.waben.option.common.model.request.resource.CommodityRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.CommodityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/6/23 20:18
 */
@RestController
@RequestMapping("/commodity")
public class CommodityController extends AbstractBaseController {

	@Resource
	private CommodityService commodityService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> queryPage(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "online", required = false) Boolean online) {
		return ok(commodityService.queryPage(page, size, online));
	}

	@RequestMapping(value = "/createUpdate", method = RequestMethod.POST)
	public ResponseEntity<?> createUpdate(@RequestBody CommodityRequest request) {
		commodityService.createUpdate(request);
		return ok();
	}
	
	@RequestMapping(value = "/clearUsedQuantity", method = RequestMethod.POST)
	public ResponseEntity<?> clearUsedQuantity() {
		commodityService.clearUsedQuantity();
		return ok();
	}
	
}
