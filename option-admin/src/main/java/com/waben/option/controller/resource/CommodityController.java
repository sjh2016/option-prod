package com.waben.option.controller.resource;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.interfacesadmin.resource.AdminCommodityAPI;
import com.waben.option.common.message.MessageFactory;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.CommodityDTO;
import com.waben.option.common.model.request.resource.CommodityRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

/**
 * @author: Peter
 * @date: 2021/6/23 20:25
 */
@RestController
@RequestMapping("/commodity")
@Api(tags = { "商品接口" })
public class CommodityController extends AbstractBaseController {

	@Resource
	private AdminCommodityAPI adminCommodityAPI;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> queryPage(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageInfo<CommodityDTO> pageInfo = adminCommodityAPI.queryPage(page, 20, true);
		pageInfo.getRecords().stream().map(commodity -> {
			if (commodity.getTotalQuantity().intValue() > 0
					&& commodity.getUsedQuantity().intValue() >= commodity.getTotalQuantity().intValue()) {
				commodity.setSoldOut(true);
			}
			return commodity;
		}).collect(Collectors.toList());
		return ok(pageInfo);
	}

	private String getName(Integer code) {
		String locale = LocaleContext.getLocale();
		String countryCode = LocaleContext.getCountryCode();
		return MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode);
	}

	@RequestMapping(value = "/createUpdate", method = RequestMethod.POST)
	public ResponseEntity<?> createUpdate(@RequestBody CommodityRequest request) {
		adminCommodityAPI.create(request);
		return ok();
	}
}
