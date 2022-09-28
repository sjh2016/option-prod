package com.waben.option.common.interfaces.thirdparty;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.push.OutsideNoticeDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "thirdparty-server", contextId = "OutsidePushAPI", qualifier = "outsidePushAPI")
public interface OutsidePushAPI extends BaseAPI {

    @RequestMapping(value = "/outside/push/notifications", method = RequestMethod.POST)
    public Response<Void> _notifications(@RequestBody OutsideNoticeDTO req);

    public default void notifications(OutsideNoticeDTO req) {
        getResponseData(_notifications(req));
    }

}
