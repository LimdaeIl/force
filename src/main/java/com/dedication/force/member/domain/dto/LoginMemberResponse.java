package com.dedication.force.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginMemberResponse {
    // GPT 직렬화 이슈 - Getter 를 붙여야 한다.
    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
