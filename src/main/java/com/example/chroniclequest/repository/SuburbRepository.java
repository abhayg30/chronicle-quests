package com.example.chroniclequest.repository;

import com.example.chroniclequest.entity.Suburb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SuburbRepository extends MongoRepository<Suburb, String> {

    @Query("{ 'name': ?0}")
    Suburb findSuburbByName(String suburb);

    List<Suburb> findByNameRegex(String suburb);
}
