package com.waben.option.core.controller.resource;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.dto.resource.NewsDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.NewsService;

@RestController
@RequestMapping("/news")
public class NewsController extends AbstractBaseController {

	@Resource
	private NewsService newsService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list(@RequestParam(value = "publishTime", required = false) String publishTime,
			@RequestParam(value = "size", defaultValue = "10") int size) {
		return ok(newsService.list(publishTime, size));
	}

	@RequestMapping(value = "/batch/save", method = RequestMethod.POST)
	public ResponseEntity<?> batchSave(@RequestBody List<NewsDTO> data) {
		newsService.batchSave(data);
		return ok();
	}

}
