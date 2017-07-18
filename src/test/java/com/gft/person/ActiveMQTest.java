package com.gft.person;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActiveMQTest {

    @LocalServerPort
    int port;

    @Autowired
    @Qualifier("brokerMessagingTemplate")
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    public void test() {
        simpMessagingTemplate.convertAndSend("/topic/test.domino", new Confirmation(false, "/test/domino"), new MessagePostProcessor() {
            @Override
            public Message<?> postProcessMessage(Message<?> message) {
                return message;
            }
        });
    }

    @Test
    public void read() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        new PersonWebSocketClient().connect(
                port,
                new Credentials("user", "test"),
                new Subscription("/topic/test.domino", new ConfirmationHandler(confirmation -> {
                    System.out.println(confirmation);
                    countDownLatch.countDown();
                }))
        );

//        new PersonWebSocketClient().connect(
//                port,
//                new Credentials("user", "test"),
//                new StompSessionHandlerAdapter() {
//                    @Override
//                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                        session.send("/topic/test.domino", new Confirmation(false, "/test/domino"));
//                    }
//                }
//        );

        countDownLatch.await(5, TimeUnit.SECONDS);
        simpMessagingTemplate.convertAndSend("/topic/test.domino", new Confirmation(false, "/test/domino"));
        countDownLatch.await(10, TimeUnit.SECONDS);
    }

//    @Test
//    public void read2() {
//        new PersonWebSocketClient().connect(port, new Credentials("user", "test"), new );
//    }
}
