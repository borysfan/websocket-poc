package com.gft.person;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.sun.javaws.JnlpxArgs.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonStompCommunicationIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    private Credentials credentials = new Credentials("user", "test");

    @LocalServerPort
    private int port;

    @Test
    public void shouldCallSecuredEndpoint() {
        //when
        ResponseEntity<List<Person>> exchange = restTemplate.exchange("https://localhost:" + port + "/api/persons", HttpMethod.GET, new HttpEntity<>(credentials.httpHeaders()), new ParameterizedTypeReference<List<Person>>() {
        });
        //then
        Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldGetResponseAfterCallWebSocketEndpoint() throws InterruptedException {
        //given
        PersonWebSocketClient personWebSocketClient = new PersonWebSocketClient(restTemplate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        //when
        personWebSocketClient.connect(port, credentials, new Connection(
                new Subscription("/topic/ws-persons", new SynchronizationHandler<>(new PersonHandler(), countDownLatch)),
                stompSession -> stompSession.send("/app/ws-persons", null)
        ));

        //then
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void shouldCallForNewSavedPersonAfterGettingConfirmation() throws InterruptedException {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        PersonWebSocketClient personWebSocketClient = new PersonWebSocketClient(restTemplate);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        personWebSocketClient.connect(port, credentials, new Subscription("/topic/person", new SynchronizationHandler<>(
                new ConfirmationHandler(confirmation -> {
                    Assert.assertNotNull(confirmation);
                    Person person = loadPerson("https://localhost:" + port + confirmation.getRestEndpoint());
                    Assert.assertEquals(firstName, person.getPersonalData().getFirsName());
                    Assert.assertEquals(lastName, person.getPersonalData().getLastName());
                }), countDownLatch)));

        //when
        savePersonalData(new PersonalData(firstName, lastName));
        //then
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void publishSubscribeModel() throws InterruptedException {
        //given
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        //when
        new PersonWebSocketClient(restTemplate).connect(port, credentials, new Connection(
                new Subscription("/topic/ws-persons", new SynchronizationHandler<>(new PersonHandler(), countDownLatch)),
                stompSession -> {}
        ));
        new PersonWebSocketClient(restTemplate).connect(port, credentials, new Connection(
                new Subscription("/topic/ws-persons", new SynchronizationHandler<>(new PersonHandler(), countDownLatch)),
                stompSession -> stompSession.send("/app/ws-persons", null)
        ));

        //then
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void pointToPointModel() throws InterruptedException {
        //given
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        StompFrameHandler mockedHandler = Mockito.mock(StompFrameHandler.class);
        //when

        new PersonWebSocketClient(restTemplate).connect(port, credentials, new Subscription("/user/queue/ws-auth", mockedHandler));

        new PersonWebSocketClient(restTemplate).connect(port, credentials, new Connection(
                new Subscription("/user/queue/ws-auth", new SynchronizationHandler<>(new PersonHandler(), countDownLatch)),
                stompSession -> stompSession.send("/app/ws-auth", null)
        ));

        //then
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
        verifyZeroInteractions(mockedHandler);
    }

    @Test
    public void notifyTest() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new PersonWebSocketClient(restTemplate).connect(port, credentials, new Connection(
                new Subscription("/user/queue/ws-welcome", new SynchronizationHandler<>(new WelcomeNewPersonHandler(), countDownLatch)),
                stompSession -> stompSession.send("/app/ws-notify", new PersonalData("Luke", "Skywalker"))
        ));
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

    private void savePersonalData(final PersonalData personalData) {
        ResponseEntity<Void> exchange = restTemplate.exchange("https://localhost:" + port + "/api/persons", HttpMethod.POST, new HttpEntity<>(personalData, credentials.httpHeaders()), Void.class);
        Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
    }

    private Person loadPerson(String url) {
        ResponseEntity<Person> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(credentials.httpHeaders()), Person.class);
        Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
        return exchange.getBody();
    }

}
