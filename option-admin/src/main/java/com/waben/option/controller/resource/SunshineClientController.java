package com.waben.option.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.resource.AdminSunshineAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.dto.resource.SunshineDTO;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.ClientSunshineRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/sunshine")
@Api(tags = { "晒单" })
public class SunshineClientController extends AbstractBaseController {

	@Resource
	private AdminSunshineAPI adminSunshineAPI;

	@ApiOperation(value = "查询最新一条未审核的晒单或者分享")
	@RequestMapping(value = "/last", method = RequestMethod.GET)
	public ResponseEntity<?> last(@RequestParam(value = "type", required = false) SunshineTypeEnum type) {
		PageInfo<SunshineDTO> pageInfo = adminSunshineAPI.queryPage(getCurrentUserId(), "3", type, null, 1, 1);
		if (pageInfo.getRecords() != null && pageInfo.getRecords().size() > 0) {
			return ok(pageInfo.getRecords().get(0));
		} else {
			return ok();
		}
	}

	@ApiOperation(value = "分享youtube", response = BannerDTO.class)
	@RequestMapping(value = "/youtube", method = RequestMethod.POST)
	public ResponseEntity<?> youtube(@RequestBody ClientSunshineRequest request) {
		adminSunshineAPI.upload(getCurrentUserId(), SunshineTypeEnum.YOUTUBE, request.getUrl());
		return ok();
	}

	@ApiOperation(value = "分享", response = BannerDTO.class)
	@RequestMapping(value = "/url", method = RequestMethod.POST)
	public ResponseEntity<?> url(@RequestBody ClientSunshineRequest request) {
		adminSunshineAPI.upload(getCurrentUserId(), request.getType(), request.getUrl());
		return ok();
	}

}
