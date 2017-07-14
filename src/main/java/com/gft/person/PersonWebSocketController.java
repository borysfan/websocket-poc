package com.gft.person;

import com.gft.shared.RecordNotFoundException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PersonWebSocketController {

    @MessageMapping("/ws-persons")
    @SendTo("/topic/ws-persons")
    public Person getPerson() throws RecordNotFoundException {
        return new Person(new PersonalData("John", "Doe"));
    }
}
