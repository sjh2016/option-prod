package com.waben.option.common.interfaces.thirdparty;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.ip.IpDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Peter
 * @date: 2021/6/1 14:18
 */
@FeignClient(value = "thirdparty-server", contextId = "IpAddressAPI", qualifier = "ipAddressAPI")
public interface IpAddressAPI extends BaseAPI {


    @RequestMapping(value = "/ip/addressByIp", method = RequestMethod.GET)
    public Response<List<IpDTO>> _addressByIp(@RequestParam("ipStr") String ipStr);

    public default List<IpDTO> addressByIp(String ipStr) {
        return getResponseData(_addressByIp(ipStr));
    }
}
