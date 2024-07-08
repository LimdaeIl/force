package com.dedication.force.service;

import com.dedication.force.domain.entity.TokenStorage;
import com.dedication.force.repository.TokenStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenStorageService {

    private final TokenStorageRepository tokenStorageRepository;

    public void addToken(String token, String tokenType) {
        TokenStorage tokenStorage = TokenStorage.builder()
                .token(token)
                .tokenType(tokenType)
                .build();

        tokenStorageRepository.save(tokenStorage);
    }
}
