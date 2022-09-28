package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.LanguageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminLanguageAPI", qualifier = "adminLanguageAPI")
public interface AdminLanguageAPI extends BaseAPI {

    @RequestMapping(value = "/language/query", method = RequestMethod.GET)
    public Response<List<LanguageDTO>> _queryLanguage();

    @RequestMapping(value = "/language/queryByCode", method = RequestMethod.GET)
    public Response<List<LanguageDTO>> _queryByCode(@RequestParam("code") String code);

    public default List<LanguageDTO> queryLanguage() {
        return getResponseData(_queryLanguage());
    }

    public default List<LanguageDTO> queryByCode(String code) {
        return getResponseData(_queryByCode(code));
    }

}
