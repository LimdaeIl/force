package com.dedication.force.common.jwt.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TokenStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private Date createdAt;

    @Builder
    public TokenStorage(String token, TokenType tokenType) {
        this.token = token;
        this.tokenType = tokenType;
        this.createdAt = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant());
    }

}
