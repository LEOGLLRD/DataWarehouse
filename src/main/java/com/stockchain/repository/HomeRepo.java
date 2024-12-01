package com.stockchain.repository;

import com.stockchain.entity.Home;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeRepo extends MongoRepository<Home, Integer> {
    Optional<Home> findByUserId(String id);

    @Query("{'userId' : ?0 }")
    @Update("{'$set' :  ?1}")
    Integer updateHome(String idUser, Home home);
}
