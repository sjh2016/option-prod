package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.request.common.SortRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayRequest;
import com.waben.option.common.model.request.payment.PaymentPassagewayUpdateEnableRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminPaymentPassagewayAPI", qualifier = "adminPaymentPassagewayAPI")
public interface AdminPaymentPassagewayAPI extends BaseAPI {

    @RequestMapping(value = "/payment_passageway/page", method = RequestMethod.GET)
    public Response<PageInfo<PaymentPassagewayDTO>> _page(@RequestParam(value = "cashType", required = false) String cashType, @RequestParam("page") int page,
                                                          @RequestParam("size") int size);

    @RequestMapping(value = "/payment_passageway/create", method = RequestMethod.POST)
    public Response<PaymentPassagewayDTO> _create(@RequestBody PaymentPassagewayRequest request);

    @RequestMapping(value = "/payment_passageway/update", method = RequestMethod.POST)
    public Response<PaymentPassagewayDTO> _update(@RequestBody PaymentPassagewayRequest request);

    @RequestMapping(value = "/payment_passageway/delete", method = RequestMethod.POST)
    public Response<Void> _delete(@RequestParam("id") Long id);

    @RequestMapping(value = "/payment_passageway/query", method = RequestMethod.GET)
    public Response<PaymentPassagewayDTO> _query(@RequestParam("id") Long id);

    @RequestMapping(value = "/payment_passageway/updateEnable", method = RequestMethod.POST)
    public Response<Void> _updateEnable(@RequestBody PaymentPassagewayUpdateEnableRequest request);

    @RequestMapping(value = "/payment_passageway/topping", method = RequestMethod.POST)
    public Response<Void> _topping(@RequestParam("id") Long id);

    @RequestMapping(value = "/payment_passageway/updateSort", method = RequestMethod.POST)
    public Response<Void> _updateSort(@RequestBody SortRequest request);

    @RequestMapping(value = "/payment_passageway/queryDisplayList", method = RequestMethod.POST)
    public Response<List<PaymentPassagewayDTO>> _queryDisplayList(@RequestParam(value = "cashTypes", required = false) PaymentCashType cashTypes);

    /********************************************************************************
     ******************************** 分割线 ****************************************
     *******************************************************************************/

    public default PageInfo<PaymentPassagewayDTO> page(String cashType, int page, int size) {
        return getResponseData(_page(cashType, page, size));
    }

    public default PaymentPassagewayDTO create(PaymentPassagewayRequest request) {
        return getResponseData(_create(request));
    }

    public default PaymentPassagewayDTO update(PaymentPassagewayRequest request) {
        return getResponseData(_update(request));
    }

    public default void delete(Long id) {
        getResponseData(_delete(id));
    }

    public default PaymentPassagewayDTO query(Long id) {
        return getResponseData(_query(id));
    }

    public default void updateEnable(PaymentPassagewayUpdateEnableRequest request) {
        getResponseData(_updateEnable(request));
    }

    public default void topping(Long id) {
        getResponseData(_topping(id));
    }

    public default void updateSort(SortRequest request) {
        getResponseData(_updateSort(request));
    }

    public default List<PaymentPassagewayDTO> queryDisplayList(PaymentCashType cashTypes) {
        return getResponseData(_queryDisplayList(cashTypes));
    }

}
