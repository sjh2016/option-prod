package com.waben.option.common.interfaces.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.user.UserBurseDTO;
import com.waben.option.common.model.enums.BurseTypeEnum;
import com.waben.option.common.model.enums.CurrencyEnum;

@FeignClient(value = "core-server", contextId = "UserBurseAPI", qualifier = "userBurseAPI")
public interface UserBurseAPI extends BaseAPI {

	@RequestMapping(method = RequestMethod.GET, value = "/user_burse/query")
	public Response<UserBurseDTO> _query(@RequestParam("userId") Long userId,
			@RequestParam("currency") CurrencyEnum currency, @RequestParam("burseType") BurseTypeEnum burseType,
			@RequestParam("payApiId") Long payApiId);

	@RequestMapping(method = RequestMethod.GET, value = "/user_burse/queryByAddress")
	public Response<UserBurseDTO> _queryByAddress(@RequestParam("address") String address);

	@RequestMapping(method = RequestMethod.POST, value = "/user_burse/create")
	public Response<Void> _create(@RequestBody UserBurseDTO req);

	public default UserBurseDTO query(Long userId, CurrencyEnum currency, BurseTypeEnum burseType, Long payApiId) {
		return getResponseData(_query(userId, currency, burseType, payApiId));
	}

	public default UserBurseDTO queryByAddress(String address) {
		return getResponseData(_queryByAddress(address));
	}

	public default void create(UserBurseDTO req) {
		getResponseData(_create(req));
	}

}
