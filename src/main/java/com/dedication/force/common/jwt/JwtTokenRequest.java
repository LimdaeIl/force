package com.dedication.force.common.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class JwtTokenRequest {
    private Long memberId;
    private String email;
    private List<String> roles;

    @Builder
    public JwtTokenRequest(Long memberId, String email, List<String> roles) {
        this.memberId = memberId;
        this.email = email;
        this.roles = roles;
    }

    public JwtTokenRequest(UserDetails userDetails) {
        this.email = userDetails.getUsername();
        this.roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
