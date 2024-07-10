package com.dedication.force.repository;

import com.dedication.force.domain.entity.TokenStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenStorageRepository extends JpaRepository<TokenStorage, Long> {

    Optional<TokenStorage> findByToken(String token);
}
