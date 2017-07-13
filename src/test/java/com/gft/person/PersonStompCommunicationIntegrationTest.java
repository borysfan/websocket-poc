package com.gft.person;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonStompCommunicationIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void shouldCallForNewSavedPersonAfterGettingConfirmation() throws InterruptedException {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        PersonWebSocketClient personWebSocketClient = new PersonWebSocketClient();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        personWebSocketClient.connectToPerson(port, new Subscription("/topic/person", new SynchronizationHandler<>(
                new ConfirmationHandler(confirmation -> {
                    Assert.assertNotNull(confirmation);
                    Person person = restTemplate.getForObject("http://localhost:" + port + confirmation.getRestEndpoint(), Person.class);
                    Assert.assertEquals(firstName, person.getPersonalData().getFirsName());
                    Assert.assertEquals(lastName, person.getPersonalData().getLastName());
                }), countDownLatch)));

        //when
        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/api/persons", new PersonalData(firstName, lastName), Void.class);

        //then
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

}
