package com.waben.option.common.interfaces.thirdparty;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.WithdrawAdminStaDTO;
import com.waben.option.common.model.dto.payment.WithdrawOrderDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.request.payment.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "core-server", contextId = "WithdrawOrderAPI", qualifier = "withdrawOrderAPI")
public interface WithdrawOrderAPI extends BaseAPI {

    @RequestMapping(value = "/withdraw_order/query", method = RequestMethod.GET)
    public Response<WithdrawOrderDTO> _query(@RequestParam("id") Long id);

    @RequestMapping(value = "/withdraw_order/queryByOrderNo", method = RequestMethod.GET)
    public Response<WithdrawOrderDTO> _queryByOrderNo(@RequestParam("orderNo") String orderNo);

    @RequestMapping(value = "/withdraw_order/place_otc_order", method = RequestMethod.POST)
    public Response<Void> _placeOtcOrder(@RequestParam("userId") Long userId,
                                         @RequestBody WithdrawOtcFrontRequest request);

    @RequestMapping(value = "/withdraw_order/underline/successful", method = RequestMethod.POST)
    public Response<Void> _underlineSuccessful(@RequestParam("auditUserId") Long auditUserId,
                                               @RequestBody WithdrawUnderlineSuccessfulRequest request);

    @RequestMapping(value = "/withdraw_order/underline/notpass", method = RequestMethod.POST)
    public Response<Void> _underlineNotpass(@RequestParam("auditUserId") Long auditUserId,
                                            @RequestBody WithdrawUnderlineNotpassRequest request);

    @RequestMapping(value = "/withdraw_order/system/process", method = RequestMethod.POST)
    public Response<Void> _systemProcess(@RequestParam("auditUserId") Long auditUserId,
                                         @RequestBody WithdrawSystemProcessRequest request);

    @RequestMapping(value = "/withdraw_order/system/successful", method = RequestMethod.POST)
    public Response<Void> _systemSuccessful(@RequestParam(value = "id", required = true) Long id,
                                            @RequestParam(value = "thirdOrderNo", required = false) String thirdOrderNo,
                                            @RequestParam(value = "hash", required = false) String hash);

    @RequestMapping(value = "/withdraw_order/system/failed", method = RequestMethod.POST)
    public Response<Void> _systemFailed(@RequestParam(value = "id", required = true) Long id);

    @RequestMapping(value = "/withdraw_order/user/page", method = RequestMethod.POST)
    public Response<PageInfo<WithdrawOrderDTO>> _userPage(@RequestParam("userId") Long userId,
                                                          @RequestBody WithdrawUserPageRequest req);

    @RequestMapping(value = "/withdraw_order/admin/page", method = RequestMethod.POST)
    public Response<PageInfo<WithdrawOrderDTO>> _adminPage(@RequestBody WithdrawAdminPageRequest req);

    @RequestMapping(value = "/withdraw_order/admin/sta", method = RequestMethod.POST)
    public Response<WithdrawAdminStaDTO> _adminSta(@RequestBody WithdrawAdminPageRequest req);

    @RequestMapping(value = "/withdraw_order/totalWithdrawAmountByUsers", method = RequestMethod.POST)
    public Response<List<WithdrawAmountDTO>> _totalWithdrawAmountByUsers(@RequestBody List<Long> uidList);
    
    @RequestMapping(value = "/withdraw_order/isWithdrawTime", method = RequestMethod.GET)
    public Response<Boolean> _isWithdrawTime();

    public default WithdrawOrderDTO query(Long id) {
        return getResponseData(_query(id));
    }

    public default List<WithdrawAmountDTO> totalWithdrawAmountByUsers(List<Long> uidList) {
        return getResponseData(_totalWithdrawAmountByUsers(uidList));
    }

    public default WithdrawOrderDTO queryByOrderNo(String orderNo) {
        return getResponseData(_queryByOrderNo(orderNo));
    }

    public default void placeOtcOrder(Long userId, WithdrawOtcFrontRequest request) {
        getResponseData(_placeOtcOrder(userId, request));
    }

    public default void underlineSuccessful(Long auditUserId, WithdrawUnderlineSuccessfulRequest request) {
        getResponseData(_underlineSuccessful(auditUserId, request));
    }

    public default void underlineNotpass(Long auditUserId, WithdrawUnderlineNotpassRequest request) {
        getResponseData(_underlineNotpass(auditUserId, request));
    }

    public default void systemProcess(Long auditUserId, WithdrawSystemProcessRequest request) {
        getResponseData(_systemProcess(auditUserId, request));
    }

    public default void systemSuccessful(Long id, String thirdOrderNo, String hash) {
        getResponseData(_systemSuccessful(id, thirdOrderNo, hash));
    }

    public default void systemFailed(Long id) {
        getResponseData(_systemFailed(id));
    }

    public default PageInfo<WithdrawOrderDTO> userPage(Long userId, WithdrawUserPageRequest req) {
        return getResponseData(_userPage(userId, req));
    }

    public default PageInfo<WithdrawOrderDTO> adminPage(WithdrawAdminPageRequest req) {
        return getResponseData(_adminPage(req));
    }

    public default WithdrawAdminStaDTO adminSta(WithdrawAdminPageRequest req) {
        return getResponseData(_adminSta(req));
    }
    
    public default Boolean isWithdrawTime() {
        return getResponseData(_isWithdrawTime());
    }

}
