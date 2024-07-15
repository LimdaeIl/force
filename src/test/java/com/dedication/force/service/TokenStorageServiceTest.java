package com.dedication.force.service;

import com.dedication.force.common.jwt.JwtTokenProvider;
import com.dedication.force.common.jwt.dto.JwtTokenRequest;
import com.dedication.force.common.jwt.entity.TokenType;
import com.dedication.force.common.jwt.dto.TokenStorageDto;
import com.dedication.force.member.domain.entity.RoleType;
import com.dedication.force.common.jwt.entity.TokenStorage;
import com.dedication.force.common.jwt.repository.TokenStorageRepository;
import com.dedication.force.common.jwt.service.TokenStorageService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DisplayName("[JWT] 서비스")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class TokenStorageServiceTest {

    @Autowired
    private TokenStorageRepository tokenStorageRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenStorageService tokenStorageService;

    @BeforeEach
    public void clean() {
        tokenStorageRepository.deleteAll();
    }

    @DisplayName("[JWT 단건 조회]: 토큰 조회하기")
    @Test
    public void GivenSavedRefreshToken_ExpectedSuccess() {
        // given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("test@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String refreshToken = jwtTokenProvider.createRefreshToken(request);

        TokenStorage tokenStorage = TokenStorage.builder()
                .token(refreshToken)
                .tokenType(TokenType.REFRESH_TOKEN)
                .build();

        tokenStorageRepository.save(tokenStorage);
        TokenStorageDto findToken = tokenStorageService.findToken(refreshToken);

        // Expected
        Assertions.assertThat(findToken.token()).isEqualTo(refreshToken);
        Assertions.assertThat(findToken.tokenType()).isEqualTo(TokenType.REFRESH_TOKEN);
    }

}