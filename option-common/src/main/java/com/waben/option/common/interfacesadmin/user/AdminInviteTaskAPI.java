package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.user.InviteTaskAuditDTO;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.request.user.InviteTaskAuditRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminInviteTaskAPI", qualifier = "adminInviteTaskAPI")
public interface AdminInviteTaskAPI extends BaseAPI {

    @RequestMapping(method = RequestMethod.GET, value = "/invite/task/queryList")
    public Response<PageInfo<InviteTaskAuditDTO>> _queryList(@RequestParam(value = "status", required = false) InviteAuditStatusEnum status,
                                                             @RequestParam(value = "day", required = false) LocalDate day,
                                                             @RequestParam(value = "uidList", required = false) List<Long> uidList,
                                                             @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("topId") String topId);

    @RequestMapping(value = "/invite/task/audit", method = RequestMethod.POST)
    public Response<Void> _audit(@RequestBody InviteTaskAuditRequest request);



    public default PageInfo<InviteTaskAuditDTO> queryList(InviteAuditStatusEnum status, LocalDate day, List<Long> uidList, int page, int size,String topId) {
        return getResponseData(_queryList(status, day, uidList, page, size,topId));
    }

    public default void audit(InviteTaskAuditRequest request) {
        getResponseData(_audit(request));
    }

}
