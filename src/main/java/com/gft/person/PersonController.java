package com.gft.person;

import com.gft.shared.RecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
public class PersonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/api/persons")
    public void persons(@RequestBody PersonalData personalData) {
        LOGGER.info("POST /persons has been called");
        personService.store(personalData);
    }

    @GetMapping("/api/persons/{identifier}")
    public Person personList(@PathVariable("identifier") UUID identifier) throws RecordNotFoundException {
        LOGGER.info("GET /persons/{} has been called", identifier);
        return personService.getPerson(identifier);
    }

}
