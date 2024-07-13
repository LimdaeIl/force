package com.dedication.force.service;

import com.dedication.force.common.exception.CustomJwtException;
import com.dedication.force.domain.entity.TokenBlacklist;
import com.dedication.force.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public Boolean isInvalidToken(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public void saveToken(String token) {
        if (tokenBlacklistRepository.existsByToken(token)) {
            throw new CustomJwtException("블랙리스트에 등록된 토큰입니다.");
        }

        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                .token(token)
                .build();

        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public List<TokenBlacklist> tokenBlacklists() {
        return tokenBlacklistRepository.findAll();
    }


}
