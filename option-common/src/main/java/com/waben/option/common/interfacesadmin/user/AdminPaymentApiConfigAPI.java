package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigSimpleDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminPaymentApiConfigAPI", qualifier = "adminPaymentApiConfigAPI")
public interface AdminPaymentApiConfigAPI extends BaseAPI {

	@RequestMapping(value = "/payment_api_config/query", method = RequestMethod.GET)
	public Response<PaymentApiConfigDTO> _query(@RequestParam("id") Long id);

	@RequestMapping(value = "/payment_api_config/list", method = RequestMethod.GET)
	public Response<List<PaymentApiConfigSimpleDTO>> _list(@RequestParam("cashType") List<PaymentCashType> cashType);

	public default PaymentApiConfigDTO query(Long id) {
		return getResponseData(_query(id));
	}

	public default List<PaymentApiConfigSimpleDTO> list(List<PaymentCashType> cashType) {
		return getResponseData(_list(cashType));
	}

}
