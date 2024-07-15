package com.dedication.force.member.domain.dto;

import com.dedication.force.member.domain.entity.Member;

import java.time.ZonedDateTime;

public record MemberDto(
        Long id,
        String email,
        String phone,
        ZonedDateTime createdAt,
        ZonedDateTime modifiedAt) {

    public MemberDto from(Member member) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getPhone(),
                member.getCreatedAt(),
                member.getModifiedAt()
        );
    }
}