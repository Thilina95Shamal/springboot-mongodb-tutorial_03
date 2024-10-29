package com.example.proj.controller.person;

import com.example.proj.model.Person;
import com.example.proj.service.person.PersonService;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Use RequestMapping on Controller when you develop based on a specific Model Eg: person,student
@RequestMapping("api/v1/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> createPerson(@RequestBody Person person){
        Person createdPerson = personService.createPerson(person);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    // @PatVariables : use for Resource Identification, for instances like IDs, and don't use it for filtering or searching.
    // @RequestParam : use for filtering and searching, don't use for Identifications like By IDs
    public ResponseEntity<?> getPersons(){
        try {
            List<Person> personList = personService.getPersons();
            return new ResponseEntity<>(personList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getByFirstName")
    // @PatVariables : use for Resource Identification, for instances like IDs, and don't use it for filtering or searching.
    // @RequestParam : use for filtering and searching, don't use for Identifications like By IDs
    public ResponseEntity<?> getByFirstName(@RequestParam String firstName){
        try {
            Person personByFistName = personService.getByFirstName(firstName);
            return new ResponseEntity<>(personByFistName, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{personId}")
    public ResponseEntity<?> deletePersonById(@PathVariable String personId){
        try {
            Person deletedPerson = personService.deletePersonById(personId);
            return new ResponseEntity<>(deletedPerson, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getPersonByAge")
    public ResponseEntity<?> getPersonByAge(@RequestParam Integer minAge,
                                            @RequestParam Integer maxAge,
                                            @RequestParam boolean includeAllFields){
        try {
            List<Person> personByAge = personService.getPersonByAge(minAge,maxAge,includeAllFields);
            return new ResponseEntity<>(personByAge, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // @RequestParam(required = false) is when that specific request is not required (it can be empty/null),
    // it's better to use false when we use a search
    @GetMapping("/search")
    public Page<?> seachPerson(@RequestParam(required = false) Optional<String> firstName,
                               @RequestParam(required = false) Optional<Integer> minAge,
                               @RequestParam(required = false) Optional<Integer> maxAge,
                               @RequestParam(required = false) Optional<String> city,
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "5") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        return personService.searchPerson(firstName,minAge,maxAge,city,pageable);
    }

    // USE CASE : Oldest Person living in a Particular City
    @GetMapping("/getOlderPersonIn")
    public ResponseEntity<?> getOlderPersonInNewYorkCity(){
        try {
            // Here the result is taken with Document
            // NOTE : Document, Not the best option for structured and well-defined data, best use DTOs
            List<Document> olderPersonInNewYork = personService.getOlderPersonInEachCity();
            return new ResponseEntity<>(olderPersonInNewYork, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        /** //Todo : try Doing this as below and use different types of aggregations like :first name and last name only by city
         * USE DTOs as below,
         * public class PersonByCity {
         *     private String city;
         *     private Person oldestPerson;
         *
         *     // Getters and setters
         * }
         * List<PersonByCity> personList = mongoTemplate.aggregate(aggregation, "person", PersonByCity.class).getMappedResults();
         * **/
    }
    // USE CASE : Population of a particular city
    @GetMapping("/getPopulationByCity")
    public ResponseEntity<?> getPopulationByCity() {
        try {
            // Here the result is taken with Document
            // NOTE : Document, Not the best option for structured and well-defined data, best use DTOs
            List<Document> olderPersonInNewYork = personService.getPopulationByCity();
            return new ResponseEntity<>(olderPersonInNewYork, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // STORING IMAGES :


}

