package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.PaymentImageDTO;
import com.waben.option.common.model.request.resource.PaymentImageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminPaymentImageAPI", qualifier = "adminPaymentImageAPI")
public interface AdminPaymentImageAPI extends BaseAPI {

    @RequestMapping(value = "/paymentImage/queryList", method = RequestMethod.GET)
    public Response<PageInfo<PaymentImageDTO>> _queryList(@RequestParam(value = "day", required = false) LocalDate day);

    @RequestMapping(value = "/paymentImage/createToUpset", method = RequestMethod.POST)
    public Response<Void> _createToUpset(@RequestBody PaymentImageRequest request);

    @RequestMapping(value = "/paymentImage/query", method = RequestMethod.GET)
    public Response<List<PaymentImageDTO>> _query();

    public default PageInfo<PaymentImageDTO> queryList(LocalDate day) {
        return getResponseData(_queryList(day));
    }

    public default void createToUpset(PaymentImageRequest request) {
        getResponseData(_createToUpset(request));
    }

    public default List<PaymentImageDTO> query() {
        return getResponseData(_query());
    }

}
