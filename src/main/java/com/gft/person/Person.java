package com.gft.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class Person {

    private UUID uuid;
    private PersonalData personalData;

    public Person(PersonalData personalData) {
        this(UUID.randomUUID(), personalData);
    }

    @JsonCreator
    public Person(@JsonProperty("identifier") UUID uuid, @JsonProperty("personalData")PersonalData personalData) {
        this.uuid = uuid;
        this.personalData = personalData;
    }

    @JsonProperty
    public UUID identifier() {
        return uuid;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(uuid, person.uuid) &&
                Objects.equals(personalData, person.personalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, personalData);
    }

    @Override
    public String toString() {
        return "Person{" +
                "uuid=" + uuid +
                ", personalData=" + personalData +
                '}';
    }
}
