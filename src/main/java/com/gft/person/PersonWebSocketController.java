package com.gft.person;

import com.gft.shared.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class PersonWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public PersonWebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/ws-persons")
    @SendTo("/topic/ws-persons")
    public Person getPerson() throws RecordNotFoundException {
        return new Person(new PersonalData("John", "Doe"));
    }

    @MessageMapping("/ws-auth")
    @SendToUser(value = "/queue/ws-auth", broadcast = false)
    public Person authenticatedUser(Principal principal) {
        return new Person(new PersonalData(principal.getName(), principal.getName()));
    }

    @MessageMapping("/ws-notify")
    public void notify(@Payload PersonalData personalData, @Headers Map<String, String> headers) {
        System.out.println(personalData);
        simpMessagingTemplate.convertAndSendToUser("user", "/queue/ws-welcome", new WelcomeNewPerson("Hello " + personalData.getFirsName() + " " + personalData.getLastName()));
    }
}
