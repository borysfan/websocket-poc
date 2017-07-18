package com.gft.person;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Arrays;

public class PersonWebSocketClient extends WebSocketStompClient {


    public PersonWebSocketClient() {
        this(new SockJsClient(Arrays.asList(new WebSocketTransport(new SSLWebSocketClient()))), new MappingJackson2MessageConverter());
    }

    public PersonWebSocketClient(RestTemplate restTemplate) {
        this(new SockJsClient(Arrays.asList(new WebSocketTransport(new SSLWebSocketClient()), new RestTemplateXhrTransport(restTemplate))), new MappingJackson2MessageConverter());
    }

    public PersonWebSocketClient(WebSocketClient webSocketClient, MessageConverter messageConverter) {
        super(webSocketClient);
        this.setMessageConverter(messageConverter);

    }

    public void connect(int port, StompSessionHandler handler) {
        connect(port, new WebSocketHttpHeaders(), handler);
    }

    public void connect(int port, Credentials credentials, StompSessionHandler handler) {
        connect(port, new WebSocketHttpHeaders(credentials.httpHeaders()), handler);
    }

    public void connect(int port, WebSocketHttpHeaders webSocketHttpHeaders, StompSessionHandler handler) {
        this.connect("wss://{host}:{port}/stomp-endpoint", webSocketHttpHeaders, handler, "localhost", port);
    }
}
