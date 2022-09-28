package com.waben.option.thirdparty.controller;

import com.waben.option.common.component.SpringContext;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.model.dto.push.OutsideNoticeDTO;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.thirdparty.service.OutsidePushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/outside/push")
public class OutsidePushController extends AbstractBaseController {

    @Resource
    private ConfigAPI configAPI;

    @RequestMapping(value = "/notifications", method = RequestMethod.POST)
    public ResponseEntity<?> notifications(@RequestBody OutsideNoticeDTO req) {
        ConfigDTO config = configAPI.queryConfig(DBConstants.OUTSIDE_PUSH_BEAN_NAME);
        if (config == null || config.getValue() == null || "".equals(config.getValue().trim())) {
            log.error("{} message push failed, outsidePushBeanName config not exist", JacksonUtil.encode(req));
        } else {
            OutsidePushService service = null;
            try {
                service = SpringContext.getBean(config.getValue().trim(), OutsidePushService.class);
            } catch (Exception ex) {
            }
            if (service != null) {
                service.notifications(req);
            } else {
                log.error("{} message push failed, outsidePushBeanName {} not found in spring container",
                        JacksonUtil.encode(req), config.getValue().trim());
            }
        }
        return ok();
    }

}
