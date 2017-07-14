package com.gft.person;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class WelcomeNewPersonHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return WelcomeNewPerson.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        WelcomeNewPerson welcomeNewPerson = (WelcomeNewPerson) payload;
        System.out.println("####################################################");
        System.out.println(((WelcomeNewPerson) payload).getMessage());
        System.out.println("####################################################");
    }
}
