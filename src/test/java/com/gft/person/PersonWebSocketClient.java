package com.gft.person;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import static java.util.Collections.singletonList;

public class PersonWebSocketClient extends WebSocketStompClient {


    public PersonWebSocketClient() {
        this(new SockJsClient(singletonList(new WebSocketTransport(new StandardWebSocketClient()))), new MappingJackson2MessageConverter());
    }

    public PersonWebSocketClient(WebSocketClient webSocketClient, MessageConverter messageConverter) {
        super(webSocketClient);
        this.setMessageConverter(messageConverter);
    }

    public void connectToPerson(int port, StompSessionHandler handler) {
        connectToPerson(port, new WebSocketHttpHeaders(), handler);
    }

    public void connectToPerson(int port, WebSocketHttpHeaders webSocketHttpHeaders, StompSessionHandler handler) {
        this.connect("ws://{host}:{port}/stomp-endpoint", webSocketHttpHeaders, handler, "localhost", port);
    }
}
