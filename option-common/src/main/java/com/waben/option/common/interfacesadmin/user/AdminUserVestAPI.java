package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.user.UserVestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminUserVestAPI", qualifier = "adminUserVestAPI")
public interface AdminUserVestAPI extends BaseAPI {

    @RequestMapping(method = RequestMethod.GET, value = "/user/vest/query")
    public Response<List<UserVestDTO>> _query(@RequestParam("userIds") List<Long> userIds);

    @RequestMapping(method = RequestMethod.POST, value = "/user/vest/bind")
    public Response<Void> _bind(@RequestBody UserVestDTO req);

    public default List<UserVestDTO> query(List<Long> userIds) {
        return getResponseData(_query(userIds));
    }

    public default void bind(UserVestDTO req) {
        getResponseData(_bind(req));
    }

}
