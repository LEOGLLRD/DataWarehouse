package com.stockchain.repository;

import com.stockchain.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepo extends MongoRepository<File, Integer> {
    Optional<File> findByFilenameAndUserEmail(String idUser, String name);
}
