package com.gft.person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class Subscription extends StompSessionHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscription.class);

    private final String destination;
    private final StompFrameHandler handler;

    public Subscription(String destination, StompFrameHandler handler) {
        this.destination = destination;
        this.handler = handler;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        LOGGER.info("Connected. Subscribing to {}", destination);
        session.subscribe(this.destination, handler);
    }
}
