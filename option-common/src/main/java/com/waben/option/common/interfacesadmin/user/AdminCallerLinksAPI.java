package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.user.CallerLinksDTO;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.user.CallerLinksRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Peter
 * @date: 2021/7/8 14:24
 */
@FeignClient(value = "admin-core-server", contextId = "AdminCallerLinksAPI", qualifier = "adminCallerLinksAPI", path = "/caller/links")
public interface AdminCallerLinksAPI extends BaseAPI {

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response<List<CallerLinksDTO>> _query(@RequestParam(value = "id", required = false) Long id,
                                                 @RequestParam(value = "type", required = false) String type,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "enable", required = false) Boolean enable);


    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Response<Void> _modify(@RequestBody CallerLinksRequest request);

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response<Void> _create(@RequestBody CallerLinksRequest request);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response<Void> _delete(@RequestBody IdRequest request);

    public default List<CallerLinksDTO> query(Long id, String type, String name, Boolean enable) {
        return getResponseData(_query(id, type, name, enable));
    }

    public default void modify(CallerLinksRequest request) {
        getResponseData(_modify(request));
    }

    public default void create(CallerLinksRequest request) {
        getResponseData(_create(request));
    }

    public default void delete(IdRequest request) {
        getResponseData(_delete(request));
    }
}
