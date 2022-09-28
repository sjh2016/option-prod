package com.waben.option.thirdparty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.enums.NewsTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.thirdparty.service.NewsGrabService;

/**
 * 资讯抓取
 */
@RestController
@RequestMapping("/news")
public class NewsGrabController extends AbstractBaseController {

	@Autowired
	@Qualifier("ftEconomyGrabService")
	private NewsGrabService newsGrabService;

	@RequestMapping(value = "/grap", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<?> grap(@RequestParam("type") NewsTypeEnum type,
			@RequestParam(value = "url", required = false) String url) {
		switch (type) {
		case ECONOMY:
			newsGrabService.grab(url);
			break;
		default:
			break;
		}
		return ok();
	}

}
