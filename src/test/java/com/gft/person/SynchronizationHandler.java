package com.gft.person;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

public class SynchronizationHandler<T extends StompFrameHandler> implements StompFrameHandler {
    private final T handler;
    private final CountDownLatch countDownLatch;

    public SynchronizationHandler(T handler, CountDownLatch countDownLatch) {
        this.handler = handler;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return handler.getPayloadType(headers);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        handler.handleFrame(headers, payload);
        countDownLatch.countDown();
    }
}
