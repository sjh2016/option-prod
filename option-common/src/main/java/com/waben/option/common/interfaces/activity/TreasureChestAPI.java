package com.waben.option.common.interfaces.activity;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.activity.TreasureChestUserJoinDTO;
import com.waben.option.common.model.request.activity.TreasureChestOpenRequest;

@FeignClient(value = "core-server", contextId = "TreasureChestAPI", qualifier = "treasureChestAPI")
public interface TreasureChestAPI extends BaseAPI {

	@RequestMapping(value = "/treasure_chest/open", method = RequestMethod.POST)
	public Response<BigDecimal> _open(@RequestBody TreasureChestOpenRequest req);

	@RequestMapping(value = "/treasure_chest/joinPage", method = RequestMethod.GET)
	public Response<PageInfo<TreasureChestUserJoinDTO>> _joinPage(@RequestParam("page") int page,
			@RequestParam("size") int size);

	public default BigDecimal open(TreasureChestOpenRequest req) {
		return getResponseData(_open(req));
	}

	public default PageInfo<TreasureChestUserJoinDTO> joinPage(int page, int size) {
		return getResponseData(_joinPage(page, size));
	}

}
