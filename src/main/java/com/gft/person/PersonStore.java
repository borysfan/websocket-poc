package com.gft.person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PersonStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonStore.class);
    private final Map<UUID, Person> personStore = new HashMap<>();

    public Person save(PersonalData personalData) {
        LOGGER.info("Trying to save personal data");
        Person person = new Person(personalData);
        personStore.put(person.identifier(), person);
        return person;
    }

    public Optional<Person> find(UUID personIdentifier) {
        return Optional.ofNullable(personStore.get(personIdentifier));
    }

    public List<Person> all() {
        return new ArrayList<>(personStore.values());
    }
}
