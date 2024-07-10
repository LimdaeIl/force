package com.dedication.force.common.jwt;

import com.dedication.force.domain.entity.RoleType;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@DisplayName("[JWT] JwtTokenProvider")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("[JWT] AccessToken 생성")
    @Test
    public void GivenJwtTokenRequest_WhenCreateAccessToken_ThenSuccess() {
        // given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        // when
        String accessToken = jwtTokenProvider.createAccessToken(request);
        System.out.println("accessToken = " + accessToken);

        // then
        Assertions.assertThat(accessToken).isNotEmpty();
    }

    @DisplayName("[JWT] RefreshToken 생성")
    @Test
    public void GivenJwtTokenRequest_WhenCreateRefreshToken_ThenSuccess() {
        // given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(request);
        System.out.println("refreshToken = " + refreshToken);

        // then
        Assertions.assertThat(refreshToken).isNotEmpty();
    }

    @DisplayName("[JWT] AccessToken 으로부터 email 확인")
    @Test
    public void GivenAccessToken_WhenValidateToken_ThenSuccess() {
        // given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(request);

        // when
        String emailFromToken = jwtTokenProvider.getEmailFromToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY());
        System.out.println("emailFromToken = " + emailFromToken);

        // then
        Assertions.assertThat(emailFromToken).isEqualTo("accessToken@naver.com");
    }

    @DisplayName("JWT token 만료 시간 조회")
    @Test
    public void GivenToken_WhenExpirationTime_ThenSuccess() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(request);
        String refreshToken = jwtTokenProvider.createRefreshToken(request);

        Date expirationTimeFromAccessToken = jwtTokenProvider.getExpirationTimeFromToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY());
        Date expirationTimeFromRefreshToken = jwtTokenProvider.getExpirationTimeFromToken(refreshToken, jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY());
        System.out.println("expirationTimeFromAccessToken = " + expirationTimeFromAccessToken);
        System.out.println("expirationTimeFromRefreshToken = " + expirationTimeFromRefreshToken);

        Assertions.assertThat(expirationTimeFromAccessToken).isNotEqualTo(expirationTimeFromRefreshToken);
        Assertions.assertThat(expirationTimeFromAccessToken).isNotNull();
        Assertions.assertThat(expirationTimeFromRefreshToken).isNotNull();
    }

    @DisplayName("JWT token 클레임 조회")
    @Test
    public void GivenToken_WhenFindAllClaims_ThenSuccess() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(request);

        Claims allClaimsFromToken = jwtTokenProvider.getAllClaimsFromToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY());
        for (Map.Entry<String, Object> entry : allClaimsFromToken.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        Assertions.assertThat(allClaimsFromToken).isNotEmpty();
        Assertions.assertThat(allClaimsFromToken.get("userId")).isEqualTo(1);
        Assertions.assertThat(allClaimsFromToken.getSubject()).isEqualTo("accessToken@naver.com");
        Assertions.assertThat(allClaimsFromToken.getIssuer()).isEqualTo("force");
        Assertions.assertThat(allClaimsFromToken.getAudience()).isEqualTo("http://localhost:8080");
    }

    @DisplayName("JWT validation 정상 수행")
    @Test
    public void GivenToken_WhenValid_ThenReturnTrue() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(request);
        System.out.println("accessToken = " + accessToken);

        // When
        Boolean isValid = jwtTokenProvider.validateToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY());

        // then
        Assertions.assertThat(isValid).isTrue();
    }

    @DisplayName("[JWT] 만료된 토큰 로그 출력")
    @Test
    public void GivenToken_WhenInValid_ThenReturnFalse() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        byte[] ACCESS_TOKEN_SECRET_KEY = "rkGU45258GGhiolLO2465TFY5345kGU45258GGhiolLO2465TFY5345".getBytes(StandardCharsets.UTF_8);

        String accessToken = jwtTokenProvider.createJWTToken(request, ACCESS_TOKEN_SECRET_KEY, 0L, "expired-token");

        // When
        Boolean isValid = jwtTokenProvider.validateToken(accessToken, ACCESS_TOKEN_SECRET_KEY);

        // then
        Assertions.assertThat(isValid).isFalse();
    }

    @DisplayName("[JWT] 유효하지 않은 시그니처 로그 출력")
    @Test
    public void GivenToken_WhenInvalidSignature_ThenReturnTrue() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        byte[] ACCESS_TOKEN_SECRET_KEY = "rkGU45258GGhiolLO2465TFY5345kGU45258GGhiolLO2465TFY5345".getBytes(StandardCharsets.UTF_8);
        byte[] FAKE_TOKEN_DUMMY_KEY = "asdq52525GGhiolLO2465TFY5345kGU45258GGhiolLO2465Thh554".getBytes(StandardCharsets.UTF_8);

        String accessToken = jwtTokenProvider.createJWTToken(request, ACCESS_TOKEN_SECRET_KEY, 0L, "expired-token");

        // When
        Boolean isValid = jwtTokenProvider.validateToken(accessToken, FAKE_TOKEN_DUMMY_KEY);

        // then
        Assertions.assertThat(isValid).isFalse();
    }

    @DisplayName("[JWT] 유효하지 않은 시그니처 로그 출력")
    @Test
    public void GivenToken_WhenGetTokenTypeFromToken_ThenIsAccessToken() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(request);

        // When
        String tokenTypeFromToken = jwtTokenProvider.getTokenTypeFromToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY());
        System.out.println("tokenTypeFromToken = " + tokenTypeFromToken);

        // Then
        Assertions.assertThat(tokenTypeFromToken).isEqualTo("access-token");
    }

    @DisplayName("[JWT 회원 권한 출력")
    @Test
    public void GivenToken_ExpectedGetRoles() {
        // Given
        JwtTokenRequest request = JwtTokenRequest.builder()
                .memberId(1L)
                .email("accessToken@naver.com")
                .roles(List.of(RoleType.USER.toString()))
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(request);

        // Expected
        List<String> memberRolesFromToken = jwtTokenProvider.getMemberRolesFromToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY());
        Assertions.assertThat(memberRolesFromToken.contains(RoleType.USER.toString())).isTrue();
    }


}