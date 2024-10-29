package com.example.proj.service.person;

import com.example.proj.model.Person;
import com.example.proj.repository.PersonRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final MongoTemplate mongoTemplate;

    public PersonServiceImpl(PersonRepository personRepository, MongoTemplate mongoTemplate) {
        this.personRepository = personRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    public List<Person> getPersons() {
        List<Person> personList = personRepository.findAll();
        if (!personList.isEmpty()) {
            return personList;
        } else {
            throw new RuntimeException("Person Not Found");
        }
    }

    @Override
    public Person getByFirstName(String firstName) {
        Optional<Person> byFirstName = personRepository.findByFirstName(firstName);
        if (byFirstName.isPresent()) {
            return byFirstName.get();
        } else {
            throw new RuntimeException("Person Not Found");
        }
    }

    @Override
    public Person deletePersonById(String personId) {
        Optional<Person> byId = personRepository.findById(personId);
        if (byId.isPresent()) {
            personRepository.deleteById(byId.get().getPersonId());
            return byId.get();
        } else {
            throw new RuntimeException("Person Not Found");
        }
    }

    @Override
    public List<Person> getPersonByAge(Integer minAge, Integer maxAge, boolean includeAllFields) {
        List<Person> personListByAge;
        if (includeAllFields) {
            personListByAge = personRepository.findPersonByAge(minAge, maxAge);
        } else {
            personListByAge = personRepository.findPersonByAgeFieldIncludingOnly(minAge, maxAge);
        }
        if (!personListByAge.isEmpty()) {
            return personListByAge;
        } else {
            throw new RuntimeException("Person Not Found");
        }
    }

    /**
     * NOTE : Search Query using Criteria
     * The below is an example of querying data using Criteria & MongoTemplate instead of using MongoRepository
     * Its almost like JDBCTemplate in MySQL.
     */
    // Best case scenarios to use this is when you use for Searching and Filtering and complex scenarios
    @Override
    public Page<Person> searchPerson(Optional<String> firstName, Optional<Integer> minAge, Optional<Integer> maxAge, Optional<String> city, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        // regex(firstName.get(),"i")) : This is Regex that matches case sensitivity. ASD,asd,AsD
        if (firstName.isPresent()) {
            criteria.add(Criteria.where("firstName").regex(firstName.get(), "i"));
        }
        // tl ,gt same as we did in mongoRepository
        if (minAge.isPresent() && maxAge.isPresent()) {
            criteria.add(Criteria.where("age").gt(minAge.get()).lt(maxAge.get()));
        }
        // "is" : should match exactly to the value
        if (city.isPresent()) {
            criteria.add(Criteria.where("addresses.city").is(city.get()));
        }

        // addCriteria : add  the specified criteria to the query object
        // new Criteria().andOperator(...) : creates a new criteria that combine all the criteria through AND operator,
        //      that means all the criteria should be satisfied to get the results documents.
        // andOperator : requires an array of criteria therefore we convert the List of criteria back to array
        //      criteria.toArray(new Criteria[0])
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        // PageableExecutionUtils.getPage() : create a pagination result of Person
        // mongoTemplate.find(query, Person.class) : retrieves a list of Person obj that matches the specified query
        // pageable : pagination info that contains page and size
        // mongoTemplate.count(query.skip(0).limit(0), Person.class) : calculate the total number of result that
        //      matches the query
        Page<Person> personList = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class),
                pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), Person.class));

        return personList;

        /** NOTE : When we use without pagination :
         * // Fetch the list of persons without pagination
         *      List<Person> personList = mongoTemplate.find(query, Person.class);
         * */
    }

    /**
     * AGGREGATION
     */
    // Cities are part of addresses, so we have to deconstruct/(flatten out) - unwind the addresses to get the city
    // Sort by the age and group by the city
    // Todo : Use different types of aggregations
    @Override
    public List<Document> getOlderPersonInEachCity() {
        // UnwindOperation : Breaks down addresses array into separate documents, duplicating the rest
        UnwindOperation unwindOperation = Aggregation.unwind("addresses");
        // SortOperation : sort Age By descending order to get the max age.
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "age");
        // GroupOperation : grouping anything accordingly.
        // first/as we can provide field ref and assignment we want to return.
        GroupOperation groupOperation = Aggregation.group("addresses.city") // Group by docs by city, each city has its oldest person
                .first(Aggregation.ROOT)         // Aggregation.Root refers to the entire document, "firstName" refers to firstName in Person
                .as("oldestPerson");       // assigns to a variable "oldestPerson"
        // Aggregation.ROOT : when you use this the _id will get a timestamp,
        //          so you have to remove this and add attributes manually to do otherwise
        Aggregation aggregation = Aggregation.newAggregation(unwindOperation,sortOperation,groupOperation);

        List<Document> personList = mongoTemplate.aggregate(aggregation,Person.class,Document.class).getMappedResults();

        return personList;
    }

    @Override
    public List<Document> getPopulationByCity() {

        UnwindOperation unwindOperation = Aggregation.unwind("addresses");

        GroupOperation groupOperation = Aggregation.group("addresses.city")
                .count().as("popCount");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"popCount");

        // Here we can manipulate which fields should be projected/provided (Get only particular fields)
        ProjectionOperation projectionOperation = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id"); // We exclude the id of the document


        // Make sure to get the right order : Issue :: No property 'popCount' found ::
        Aggregation aggregation = Aggregation.newAggregation(unwindOperation,groupOperation,sortOperation,projectionOperation);

        List<Document> personList = mongoTemplate.aggregate(aggregation,Person.class,Document.class).getMappedResults();

        return personList;

    }


}
