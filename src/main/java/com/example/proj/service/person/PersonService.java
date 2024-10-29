package com.example.proj.service.person;

import com.example.proj.model.Person;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person createPerson(Person person);

    List<Person> getPersons();

    Person getByFirstName(String firstName);

    Person deletePersonById(String personId);

    List<Person> getPersonByAge(Integer minAge, Integer maxAge, boolean includeAllFields);

    Page<Person> searchPerson(Optional<String> firstName, Optional<Integer> minAge, Optional<Integer> maxAge, Optional<String> city, Pageable pageable);

    List<Document> getOlderPersonInEachCity();

    List<Document> getPopulationByCity();
}
