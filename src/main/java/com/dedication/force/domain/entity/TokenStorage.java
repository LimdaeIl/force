package com.dedication.force.domain.entity;

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

    private String tokenType;

    private Date createdAt;

    @Builder
    public TokenStorage(String token, String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
        this.createdAt = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant());
    }
}
