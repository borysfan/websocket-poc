package com.gft.person;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        ResponseEntity<List<Person>> exchange = restTemplate.exchange("http://localhost:" + port + "/api/persons", HttpMethod.GET, new HttpEntity<>(credentials.httpHeaders()), new ParameterizedTypeReference<List<Person>>() {
        });
        //then
        Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldCallForNewSavedPersonAfterGettingConfirmation() throws InterruptedException {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        PersonWebSocketClient personWebSocketClient = new PersonWebSocketClient();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        personWebSocketClient.connectToPerson(port, new WebSocketHttpHeaders(credentials.httpHeaders()), new Subscription("/topic/person", new SynchronizationHandler<>(
                new ConfirmationHandler(confirmation -> {
                    Assert.assertNotNull(confirmation);
                    Person person = loadPerson("http://localhost:" + port + confirmation.getRestEndpoint());
                    Assert.assertEquals(firstName, person.getPersonalData().getFirsName());
                    Assert.assertEquals(lastName, person.getPersonalData().getLastName());
                }), countDownLatch)));

        //when
        savePersonalData(new PersonalData(firstName, lastName));
        //then
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

    private void savePersonalData(final PersonalData personalData) {
        ResponseEntity<Void> exchange = restTemplate.exchange("http://localhost:" + port + "/api/persons", HttpMethod.POST, new HttpEntity<>(personalData, credentials.httpHeaders()), Void.class);
        Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
    }

    private Person loadPerson(String url) {
        ResponseEntity<Person> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(credentials.httpHeaders()), Person.class);
        Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
        return exchange.getBody();
    }

}
