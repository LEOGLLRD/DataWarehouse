package com.stockchain.repository;

import com.stockchain.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, Integer> {
    Optional<User> findBy_id(String email);
}
