package com.dedication.force.common.jwt.dto;

import com.dedication.force.common.jwt.entity.TokenType;
import com.dedication.force.common.jwt.entity.TokenStorage;

import java.util.Date;

public record TokenStorageDto(Long id, String token, TokenType tokenType, Date createdAt) {

    public TokenStorage toEntity() {
        return TokenStorage.builder()
                .token(token)
                .tokenType(tokenType)
                .build();
    }

    public static TokenStorageDto from(TokenStorage tokenStorage) {
        return new TokenStorageDto(
                tokenStorage.getId(),
                tokenStorage.getToken(),
                tokenStorage.getTokenType(),
                tokenStorage.getCreatedAt());
    }
}
