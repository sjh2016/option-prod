package com.waben.option.controller.summary;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.summary.FundDataService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@RestController
//@RequestMapping("/fundData")
public class FundDataController extends AbstractBaseController {

	@Resource
	private FundDataService fundDataService;

	@ApiOperation(value = "查询资金统计列表", response = BannerDTO.class)
	@RequestMapping(value = "/queryList", method = RequestMethod.GET)
	public ResponseEntity<?> queryList(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@ApiParam(name = "page", value = "页码") int page, @ApiParam(name = "size", value = "每页数量") int size) {
		boolean isAll = false;
		if (getCurrentUserId().equals(1420302342074400766L)) {
			isAll = true;
		}
		PageInfo<FundDataDTO> pageData = fundDataService.queryList(startTime, endTime, page, size);
		if (pageData != null && pageData.getRecords() != null) {
			for (FundDataDTO data : pageData.getRecords()) {
				if(isAll) {
					data.setTotalPayAmount(data.getAllTotalPayAmount());
					data.setTotalPayCount(data.getAllTotalPayCount());
					data.setTotalPayPeopleCount(data.getAllTotalPayPeopleCount());
				}
				data.setAllTotalPayAmount(BigDecimal.ZERO);
				data.setAllTotalPayCount(0);
				data.setAllTotalPayPeopleCount(0);
			}
		}
		return ok(pageData);
	}
}
