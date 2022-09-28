package com.waben.option.common.interfaces.client;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.push.PushChannelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "client-server", contextId = "PushAPI", qualifier = "pushAPI")
public interface ClientPushAPI extends BaseAPI {

    @RequestMapping(value = "/push/data", method = RequestMethod.POST)
    Response<Void> _push(@RequestBody PushChannelDTO pushData);

    default void push(PushChannelDTO pushData) {
        _push(pushData);
    }

}
