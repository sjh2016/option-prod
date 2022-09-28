package com.waben.option.common.interfacesadmin.activity;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;

@FeignClient(value = "admin-core-server", contextId = "AdminAllowanceDetailAPI", qualifier = "adminAllowanceDetailAPI")
public interface AdminAllowanceDetailAPI extends BaseAPI {

	@RequestMapping(value = "/allowanceDetail/distribute", method = RequestMethod.POST)
	public Response<Void> _distribute(@RequestParam("orderId") Long orderId, @RequestParam("userId") Long userId,
			@RequestParam("cycle") Integer cycle, @RequestParam("returnRate") BigDecimal returnRate,
			@RequestParam("amount") BigDecimal amount, @RequestParam("type") Integer type);

	public default void distribute(Long orderId, Long userId, Integer cycle, BigDecimal returnRate, BigDecimal amount, Integer type) {
		getResponseData(_distribute(orderId, userId, cycle, returnRate, amount, type));
	}

}
