package com.stockchain.repository;

import com.stockchain.entity.File;
import com.stockchain.entity.Home;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeRepo extends MongoRepository<Home, Integer> {
}
