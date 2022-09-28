package com.waben.option.controller.resource;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.resource.SunshineAPI;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.SunshineRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin/sunshine")
@Api(tags = { "晒单" })
public class SunshineController extends AbstractBaseController {

	@Resource
	private SunshineAPI sunshineService;

	@ApiOperation(value = "获取晒单列表", response = BannerDTO.class)
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "enable", required = false) String enable,
			@RequestParam(value = "type", required = false) SunshineTypeEnum type,
			@RequestParam(value = "localDate", required = false) LocalDate localDate,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
		return ok(sunshineService.queryPage(userId, enable, type, localDate, page, size));
	}

//	@ApiOperation(value = "晒单审核", response = BannerDTO.class)
//	@RequestMapping(value = "/audit", method = RequestMethod.POST)
//	public ResponseEntity<?> audit(@RequestBody SunshineRequest request) {
//		if (request.getId() == null)
//			throw new ServerException(1030);
//		sunshineService.audit(request);
//		return ok();
//	}

}
