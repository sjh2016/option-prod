package com.waben.option.core.thread;

import com.waben.option.common.interfaces.thirdparty.OutsidePushAPI;
import com.waben.option.common.model.dto.push.OutsideMessageDTO;
import com.waben.option.common.model.dto.push.OutsideNoticeDTO;
import com.waben.option.common.model.dto.push.OutsidePushMessageTemplateDTO;
import com.waben.option.common.thread.MessageQueue;
import com.waben.option.common.util.StringTemplateUtil;
import com.waben.option.core.service.resource.ConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
public class OutsidePushMessageQueue extends MessageQueue<OutsideMessageDTO> {

    @Resource
    private OutsidePushAPI outsidePushAPI;

    @Resource
    private ConfigService configService;

    @Override
    protected void execute(OutsideMessageDTO message) {
        OutsidePushMessageTemplateDTO template = configService.queryOutsidePushMessageTemplate(message.getType());
        if (template != null) {
            OutsideNoticeDTO req = new OutsideNoticeDTO();
            req.setUserIds(message.getUserIds());
            req.setTitle(template.getTitle());
            req.setContent(StringTemplateUtil.format(template.getMessage(), message.getParams()));
            outsidePushAPI.notifications(req);
        } else {
            log.info("{} {} outside push message template not exist!", message.getType(), message.getReferenceId());
        }
    }

}
