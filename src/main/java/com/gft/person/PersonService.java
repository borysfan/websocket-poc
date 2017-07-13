package com.gft.person;

import com.gft.shared.RecordNotFoundException;
import com.gft.shared.SafeSleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PersonService {

    private final PersonStore personStore;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public PersonService(PersonStore personStore, SimpMessagingTemplate simpMessagingTemplate) {
        this.personStore = personStore;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Async
    public void store(PersonalData personalData) {
        CompletableFuture.runAsync(() -> {
            new SafeSleep(2000).sleep();
            Person savedPerson = personStore.save(personalData);
            simpMessagingTemplate.convertAndSend("/topic/person", new Confirmation(true, "/api/persons/" + savedPerson.identifier()));
        });
    }

    public List<Person> persons() {
        return personStore.all();
    }

    public Person getPerson(UUID identifier) throws RecordNotFoundException {
        return personStore.find(identifier).orElseThrow(RecordNotFoundException::new);
    }
}
