package com.stockchain.repository;

import com.stockchain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepo extends JpaRepository<File, Integer> {
    Optional<File> findByIdUser(Integer idUser);
}
