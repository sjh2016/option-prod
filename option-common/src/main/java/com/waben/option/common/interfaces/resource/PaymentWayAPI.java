package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.PaymentWayDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "core-server", contextId = "PaymentWayAPI", qualifier = "paymentWayAPI")
public interface PaymentWayAPI extends BaseAPI {

    @RequestMapping(value = "/payment/queryList", method = RequestMethod.GET)
    public Response<PageInfo<PaymentWayDTO>> _queryList();

    public default PageInfo<PaymentWayDTO> queryList() {
        return getResponseData(_queryList());
    }

}
