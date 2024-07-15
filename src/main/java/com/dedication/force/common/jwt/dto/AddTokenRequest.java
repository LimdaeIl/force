package com.dedication.force.common.jwt.dto;

import lombok.Builder;

import java.util.Date;

public record AddTokenRequest(String token, String tokenType, Date createdAt) {

    @Builder
    public AddTokenRequest {}
}
