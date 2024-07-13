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
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_blacklist_id", nullable = false, updatable = false)
    private Long id;

    @Column(length = 512, nullable = false, updatable = false)
    private String token;

    @Column(updatable = false, nullable = false)
    private Date createdAt;

    @Builder
    public TokenBlacklist(String token) {
        this.token = token;
        this.createdAt = Date.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant());
    }
}
