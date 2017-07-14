package com.gft.person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.function.Consumer;

public class Connection extends StompSessionHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscription.class);
    private final Subscription subscription;
    private final Consumer<StompSession> consumer;

    public Connection(Subscription subscription, Consumer<StompSession> consumer) {
        this.subscription = subscription;
        this.consumer = consumer;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        subscription.afterConnected(session, connectedHeaders);
        consumer.accept(session);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        LOGGER.error("Web-socket exception", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        LOGGER.error("Web-socket transport error", exception);
    }

}
