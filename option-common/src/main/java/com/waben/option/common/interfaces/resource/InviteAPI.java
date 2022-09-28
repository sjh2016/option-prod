package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.InviteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "core-server", contextId = "InviteAPI", qualifier = "inviteAPI")
public interface InviteAPI extends BaseAPI {

    @RequestMapping(value = "/invite/queryList", method = RequestMethod.GET)
    public Response<PageInfo<InviteDTO>> _queryList();

    public default PageInfo<InviteDTO> queryList() {
        return getResponseData(_queryList());
    }

}
