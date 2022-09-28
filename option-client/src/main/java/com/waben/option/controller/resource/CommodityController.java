package com.waben.option.controller.resource;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.component.LocaleContext;
import com.waben.option.common.interfaces.resource.CommodityAPI;
import com.waben.option.common.message.MessageFactory;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.dto.resource.CommodityDTO;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/commodity")
@Api(tags = { "商品接口" })
public class CommodityController extends AbstractBaseController {

	@Resource
	private CommodityAPI commodityAPI;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> queryPage(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageInfo<CommodityDTO> pageInfo = commodityAPI.queryPage(page, 20, true);
		pageInfo.getRecords().stream().map(commodity -> {
			if (commodity.getTotalQuantity().intValue() > 0
					&& commodity.getUsedQuantity().intValue() >= commodity.getTotalQuantity().intValue()) {
				commodity.setSoldOut(true);
			}
			return commodity;
		}).collect(Collectors.toList());
		return ok(pageInfo);
	}

	@RequestMapping(value = "/hot", method = RequestMethod.GET)
	public ResponseEntity<?> hot() {
		return ok(commodityAPI.hot());
	}

	@SuppressWarnings("unused")
	private String getName(Integer code) {
		String locale = LocaleContext.getLocale();
		String countryCode = LocaleContext.getCountryCode();
		return MessageFactory.INSTANCE.getMessage(code + "", locale, countryCode);
	}

//	@RequestMapping(value = "/createUpdate", method = RequestMethod.POST)
//	public ResponseEntity<?> createUpdate(@RequestBody CommodityRequest request) {
//		commodityAPI.create(request);
//		return ok();
//	}

}
