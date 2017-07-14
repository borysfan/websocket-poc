package com.gft.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WelcomeNewPerson {

    private final String message;

    @JsonCreator
    public WelcomeNewPerson(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


}
