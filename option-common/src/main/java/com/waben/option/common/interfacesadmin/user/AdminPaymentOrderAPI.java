package com.waben.option.common.interfacesadmin.user;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.PaymentAdminStaDTO;
import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.payment.PayCoinSuccessRequest;
import com.waben.option.common.model.request.payment.PayOtcSuccessRequest;
import com.waben.option.common.model.request.payment.PaymentAdminPageRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.PaymentUserPageRequest;

@FeignClient(value = "admin-core-server", contextId = "AdminPaymentOrderAPI", qualifier = "adminPaymentOrderAPI")
public interface AdminPaymentOrderAPI extends BaseAPI {

    @RequestMapping(value = "/payment_order/query", method = RequestMethod.GET)
    public Response<PaymentOrderDTO> _query(@RequestParam("id") Long id);

    @RequestMapping(value = "/payment_order/last", method = RequestMethod.GET)
    public Response<PaymentOrderDTO> _last(@RequestParam("userId") Long userId);

    @RequestMapping(value = "/payment_order/queryByOrderNo", method = RequestMethod.GET)
    public Response<PaymentOrderDTO> _queryByOrderNo(@RequestParam("orderNo") String orderNo);

    @RequestMapping(value = "/payment_order/queryByThirdOrderNo", method = RequestMethod.GET)
    public Response<PaymentOrderDTO> _queryByThirdOrderNo(@RequestParam("thirdOrderNo") String thirdOrderNo);

    @RequestMapping(value = "/payment_order/hasThirdOrderNo", method = RequestMethod.GET)
    public Response<Boolean> _hasThirdOrderNo(@RequestParam("thirdOrderNo") String thirdOrderNo);

    @RequestMapping(value = "/payment_order/payOtcSuccess", method = RequestMethod.POST)
    public Response<Void> _payOtcSuccess(@RequestBody PayOtcSuccessRequest req);

    @RequestMapping(value = "/payment_order/payCoinSuccess", method = RequestMethod.POST)
    public Response<Void> _payCoinSuccess(@RequestBody PayCoinSuccessRequest req);

    @RequestMapping(value = "/payment_order/user/page", method = RequestMethod.POST)
    public Response<PageInfo<PaymentOrderDTO>> _userPage(@RequestParam("userId") Long userId,
                                                         @RequestBody PaymentUserPageRequest req);

    @RequestMapping(value = "/payment_order/admin/page", method = RequestMethod.POST)
    public Response<PageInfo<PaymentOrderDTO>> _adminPage(@RequestBody PaymentAdminPageRequest req);

    @RequestMapping(value = "/payment_order/admin/sta", method = RequestMethod.POST)
    public Response<PaymentAdminStaDTO> _adminSta(@RequestBody PaymentAdminPageRequest req);

    @RequestMapping(value = "/payment_order/close", method = RequestMethod.POST)
    public Response<PaymentAdminStaDTO> _close(@RequestBody IdRequest req);

    @RequestMapping(value = "/payment_order/createOrder", method = RequestMethod.POST)
    public Response<PaymentOrderDTO> _createOrder(@RequestBody PaymentOrderDTO req);

    @RequestMapping(value = "/payment_order/updateThirdInfo", method = RequestMethod.POST)
    public Response<Void> _updateThirdInfo(@RequestBody PaymentUpdateThirdInfoRequest req);

    @RequestMapping(value = "/payment_order/inviteRechargePeopleBySymbol", method = RequestMethod.GET)
    public Response<Integer> _inviteRechargePeopleBySymbol(@RequestParam("symbol") String symbol);

    @RequestMapping(value = "/payment_order/totalRechargeAmountByUsers", method = RequestMethod.POST)
    public Response<List<WithdrawAmountDTO>> _totalRechargeAmountByUsers(@RequestBody List<Long> uidList);

    public default Integer inviteRechargePeopleBySymbol(String symbol) {
        return getResponseData(_inviteRechargePeopleBySymbol(symbol));
    }

    public default List<WithdrawAmountDTO> inviteRechargePeopleByUsers(List<Long> uidList) {
        return getResponseData(_totalRechargeAmountByUsers(uidList));
    }

    public default PaymentOrderDTO query(Long id) {
        return getResponseData(_query(id));
    }

    public default PaymentOrderDTO queryByOrderNo(String orderNo) {
        return getResponseData(_queryByOrderNo(orderNo));
    }

    public default PaymentOrderDTO queryByThirdOrderNo(String thirdOrderNo) {
        return getResponseData(_queryByThirdOrderNo(thirdOrderNo));
    }

    public default boolean hasThirdOrderNo(String thirdOrderNo) {
        return getResponseData(_hasThirdOrderNo(thirdOrderNo));
    }

    public default void payOtcSuccess(PayOtcSuccessRequest req) {
        getResponseData(_payOtcSuccess(req));
    }

    public default void payCoinSuccess(PayCoinSuccessRequest req) {
        getResponseData(_payCoinSuccess(req));
    }

    public default PageInfo<PaymentOrderDTO> userPage(Long userId, PaymentUserPageRequest req) {
        return getResponseData(_userPage(userId, req));
    }

    public default PageInfo<PaymentOrderDTO> adminPage(PaymentAdminPageRequest req) {
        return getResponseData(_adminPage(req));
    }

    public default PaymentAdminStaDTO adminSta(PaymentAdminPageRequest req) {
        return getResponseData(_adminSta(req));
    }

    public default void close(IdRequest req) {
        getResponseData(_close(req));
    }

    public default PaymentOrderDTO createOrder(PaymentOrderDTO req) {
        return getResponseData(_createOrder(req));
    }

    public default void updateThirdInfo(PaymentUpdateThirdInfoRequest req) {
        getResponseData(_updateThirdInfo(req));
    }

    public default PaymentOrderDTO last(Long userId) {
        return getResponseData(_last(userId));
    }

}
