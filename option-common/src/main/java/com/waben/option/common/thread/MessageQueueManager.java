package com.waben.option.common.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class MessageQueueManager<T extends MessageQueue, M> {

    private int threadCount;

    private Object[] queues;

    public final static int DEFAULT_THREAD_COUNT = 5;

    public MessageQueueManager() {
        this(DEFAULT_THREAD_COUNT);
    }

    public MessageQueueManager(int threadCount) {
        this.threadCount = threadCount;
    }

    public void init() {
        queues = new Object[threadCount];
        for (int i = 0; i < threadCount; i++) {
            T queue = newInstance();
            if (queue.getName() == null) {
                queue.setName(queue.getClass().getName() + "-" + i);
            }
            queues[i] = queue;
            queue.start();
        }
    }

    protected abstract T newInstance();

    private final static AtomicInteger atomicInteger = new AtomicInteger(0);

    public void addMessage(M message) {
        ((T)queues[atomicInteger.incrementAndGet() % threadCount]).addMessage(message);
    }

}
