package com.dedication.force.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    ACCESS_TOKEN("액세스 토큰"),
    REFRESH_TOKEN("리프레시 토큰");

    public final String token;
}
