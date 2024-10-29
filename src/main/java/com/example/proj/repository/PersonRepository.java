package com.example.proj.repository;

import com.example.proj.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends MongoRepository<Person,String> {
    /**
     * NOTE : WHEN to Use WHAT { @Aggregation ,@Query}
     *  In the previous Project Tutorial 002 I used @Aggregation(pipeline = , which is most efficient for complex scenarios
     *      like , $group, $match, $sort, and $project in a single query, not for simple queries. (like age match only)
     *  In This Project have used @Query(value =, which is actually better for simple queries that matches a specified
     *      criteria.
     * */

    @Query("{'firstName': ?0 }")
    Optional<Person> findByFirstName(String firstName);

    // We can also achieve the grater than less than and many more mongo Operation when we use Value = "",
    // which provides a better understanding of mongo queries
    @Query(value = "{'age': { $gt : ?0, $lt : ?1 }}")
    List<Person> findPersonByAge(Integer minAge, Integer maxAge);

    // Here we can get the fields that we need, specifying them.
    // We can define them by not need/need by 0/1 respectively
    @Query(value = "{'age': { $gt : ?0, $lt : ?1 }}",
            fields = "{'addresses': 0}")
    List<Person> findPersonByAgeFieldIncludingOnly(Integer minAge, Integer maxAge);
}
