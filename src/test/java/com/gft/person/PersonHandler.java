package com.gft.person;

import org.junit.Assert;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class PersonHandler implements StompFrameHandler {

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Person.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        Person person = (Person) payload;
        Assert.assertNotNull(person);
        System.out.println(person);
    }
}
