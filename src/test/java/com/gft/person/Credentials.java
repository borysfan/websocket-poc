package com.gft.person;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.Base64;

public class Credentials {
    private final String username;
    private final String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public HttpHeaders httpHeaders() {
        String notEncoded = this.username + ":" + this.password;
        String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + encodedAuth);
        return headers;
    }

}
