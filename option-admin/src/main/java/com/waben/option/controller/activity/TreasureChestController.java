package com.waben.option.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.activity.AdminTreasureChestAPI;
import com.waben.option.common.model.request.activity.TreasureChestOpenRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "宝箱" })
@RestController
@RequestMapping("/treasure_chest")
public class TreasureChestController extends AbstractBaseController {

	@Resource
	private AdminTreasureChestAPI adminTreasureChestAPI;

	@RequestMapping(value = "/open", method = RequestMethod.POST)
	public ResponseEntity<?> open(@RequestBody TreasureChestOpenRequest req) {
		req.setUserId(getCurrentUserId());
		return ok(adminTreasureChestAPI.open(req));
	}

	@RequestMapping(value = "/joinPage", method = RequestMethod.GET)
	public ResponseEntity<?> joinPage(@RequestParam("page") int page, @RequestParam("size") int size) {
		return ok(adminTreasureChestAPI.joinPage(page, size));
	}

}
