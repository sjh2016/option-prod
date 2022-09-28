package com.waben.option.common.interfaces.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.account.AccountMovementDTO;
import com.waben.option.common.model.request.user.UserAccountMovementApplyRequest;
import com.waben.option.common.model.request.user.UserAccountMovementAuditRequest;
import com.waben.option.common.model.request.user.UserAccountMovementRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "AccountMovementAPI", qualifier = "accountMovementAPI", path = "/user/movement")
public interface AccountMovementAPI extends BaseAPI {

    @RequestMapping(method = RequestMethod.POST, value = "/page")
    Response<PageInfo<AccountMovementDTO>> _page(@RequestBody UserAccountMovementRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/apply")
    Response<Void> _apply(@RequestParam("applyUserId") Long applyUserId,
                          @RequestBody UserAccountMovementApplyRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/audit")
    Response<Void> _audit(@RequestParam("auditUserId") Long auditUserId,
                          @RequestBody UserAccountMovementAuditRequest req);

    public default PageInfo<AccountMovementDTO> page(UserAccountMovementRequest req) {
        return getResponseData(_page(req));
    }

    public default void apply(Long applyUserId, UserAccountMovementApplyRequest req) {
        getResponseData(_apply(applyUserId, req));
    }

    public default void audit(Long auditUserId, UserAccountMovementAuditRequest req) {
        getResponseData(_audit(auditUserId, req));
    }

}
