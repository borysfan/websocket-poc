package com.gft.person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class ConfirmationHandler implements StompFrameHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationHandler.class);

    private final Consumer<Confirmation> consumer;

    public ConfirmationHandler(Consumer<Confirmation> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        LOGGER.info("Handling confirmation");
        Confirmation confirmation = (Confirmation) payload;
        consumer.accept(confirmation);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Confirmation.class;
    }
}
