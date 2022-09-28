package com.waben.option.controller.summary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import com.waben.option.common.model.dto.summary.UserDataDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.summary.FundDataService;
import com.waben.option.service.summary.UserDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/userData")
@Api(tags = { "用户数据统计" })
public class UserDataController extends AbstractBaseController {

	@Resource
	private UserDataService userDataService;

	@Resource
	private FundDataService fundDataService;

	@ApiOperation(value = "查询用户数据统计列表", response = BannerDTO.class)
	@RequestMapping(value = "/queryList", method = RequestMethod.GET)
	public ResponseEntity<?> queryList(@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@ApiParam(name = "page", value = "页码") int page, @ApiParam(name = "size", value = "每页数量") int size) {
		boolean isAll = false;
		if (getCurrentUserId().equals(1420302342074400766L)) {
			isAll = true;
		}
		PageInfo<FundDataDTO> pageData = fundDataService.queryList(startTime, endTime, page, size);
		List<UserDataDTO> list = new ArrayList<>();
		if (pageData != null && pageData.getRecords() != null) {
			for (FundDataDTO data : pageData.getRecords()) {
				UserDataDTO dto = new UserDataDTO();
				dto.setDay(data.getDay());
				dto.setRegisterNumber(data.getRegisterNumber());
				if (isAll) {
					dto.setPaymentAmount(data.getAllTotalPayAmount());
					dto.setPaymentUserCount(data.getAllTotalPayPeopleCount());
				} else {
					dto.setPaymentAmount(data.getTotalPayAmount());
					dto.setPaymentUserCount(data.getTotalPayPeopleCount());
				}
				dto.setBeInvites(data.getInviteRegister());
				dto.setBeInvitesPaymentAmount(BigDecimal.ZERO);
				list.add(dto);
			}
		}
		return ok(new PageInfo<UserDataDTO>(list, pageData.getTotal(),  pageData.getPage(), pageData.getSize()));
//		return ok(userDataService.queryList(startTime, endTime, page, size));
	}

}
