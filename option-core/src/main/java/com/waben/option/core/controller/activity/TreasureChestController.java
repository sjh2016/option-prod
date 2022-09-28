package com.waben.option.core.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.request.activity.TreasureChestOpenRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.activity.TreasureChestService;

@RestController
@RequestMapping("/treasure_chest")
public class TreasureChestController extends AbstractBaseController {

	@Resource
	private TreasureChestService treasureChestService;

	@RequestMapping(value = "/open", method = RequestMethod.POST)
	public ResponseEntity<?> open(@RequestBody TreasureChestOpenRequest req) {
		return ok(treasureChestService.open(req));
	}

	@RequestMapping(value = "/joinPage", method = RequestMethod.GET)
	public ResponseEntity<?> joinPage(@RequestParam("page") int page, @RequestParam("size") int size) {
		return ok(treasureChestService.joinPage(page, size));
	}

}
