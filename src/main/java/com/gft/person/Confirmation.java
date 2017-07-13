package com.gft.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Confirmation {
    private final Boolean success;
    private final String restEndpoint;

    @JsonCreator
    public Confirmation(@JsonProperty("success") Boolean success, @JsonProperty("restEndpoint") String restEndpoint) {
        this.success = success;
        this.restEndpoint = restEndpoint;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

    @Override
    public String toString() {
        return "Confirmation{" +
                "success=" + success +
                ", restEndpoint='" + restEndpoint + '\'' +
                '}';
    }
}
