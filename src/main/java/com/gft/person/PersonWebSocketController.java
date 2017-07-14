package com.gft.person;

import com.gft.shared.RecordNotFoundException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class PersonWebSocketController {

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
}
