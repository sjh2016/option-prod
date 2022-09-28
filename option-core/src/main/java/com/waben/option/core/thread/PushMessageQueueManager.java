package com.waben.option.core.thread;

import com.waben.option.common.model.dto.push.PushDataDTO;
import com.waben.option.common.thread.MessageQueueManager;


public class PushMessageQueueManager extends MessageQueueManager<PushMessageQueue, PushDataDTO> {

    public PushMessageQueueManager() {
        super();
    }

    public PushMessageQueueManager(int threadCount) {
        super(threadCount);
    }

    @Override
    protected PushMessageQueue newInstance() {
        return new PushMessageQueue();
    }

}
