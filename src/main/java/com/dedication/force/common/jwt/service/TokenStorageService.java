package com.dedication.force.common.jwt.service;

import com.dedication.force.common.exception.CustomJwtException;
import com.dedication.force.common.jwt.entity.TokenType;
import com.dedication.force.common.jwt.dto.TokenStorageDto;
import com.dedication.force.common.jwt.entity.TokenStorage;
import com.dedication.force.common.jwt.repository.TokenStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenStorageService {

    private final TokenStorageRepository tokenStorageRepository;

    public void addToken(String token, TokenType tokenType) {
        TokenStorage tokenStorage = TokenStorage.builder()
                .token(token)
                .tokenType(tokenType)
                .build();

        tokenStorageRepository.save(tokenStorage);
    }

    public TokenStorageDto findToken(String token) {
        TokenStorage tokenStorage = tokenStorageRepository.findByToken(token)
                .orElseThrow(() -> new CustomJwtException("존재하지 않는 토큰입니다."));

        return TokenStorageDto.from(tokenStorage);
    }
}
