package com.waben.option.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.resource.NewsAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/news")
public class NewsController extends AbstractBaseController {

	@Resource
	private NewsAPI newsService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list(@RequestParam(value = "publishTime", required = false) String publishTime,
			@RequestParam(value = "size", defaultValue = "10") int size) {
		return ok(newsService.list(publishTime, size));
	}

}
