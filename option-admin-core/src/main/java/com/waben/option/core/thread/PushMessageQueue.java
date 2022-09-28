package com.waben.option.core.thread;

import com.waben.option.common.model.dto.push.PushDataDTO;
import com.waben.option.common.thread.MessageQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushMessageQueue extends MessageQueue<PushDataDTO> {

    @Override
    protected void execute(PushDataDTO message) {
        log.info(getName() + ": " + message.toString());
//        SpringContext.getBean(SubscriberAPI.class).push(message);
    }

}
