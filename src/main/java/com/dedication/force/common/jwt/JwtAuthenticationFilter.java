package com.dedication.force.common.jwt;

import com.dedication.force.common.jwt.dto.JwtTokenRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY())) {
            // Access Token이 유효한 경우,
            authenticateWithToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY(), request);
        } else {
            // Access Token이 유효하지 않은 경우, Refresh Token을 검사
            String refreshToken = getRefreshTokenFromRequest(request);
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken, jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY())) {
                // Refresh Token이 유효한 경우, 새로운 Access Token 발급
                String email = jwtTokenProvider.getEmailFromToken(refreshToken, jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY());

                // 새로운 Access Token 발급 로직을 서비스에서 호출
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                String newAccessToken = jwtTokenProvider.createAccessToken(new JwtTokenRequest(userDetails));

                response.setHeader("Authorization", "Bearer " + newAccessToken);
                authenticateWithToken(newAccessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY(), request);
            }
        }

        // Refresh Token이 유효하지 않은 경우는 JwtAuthenticationFilter 클래스에서 validateToken 메서드의 반환 값이 false일 때 처리됩니다. 코드의 흐름상 validateToken 메서드가 false를 반환하면 아무 작업도 수행되지 않으며, 다음 필터로 넘어가게 됩니다.
        // 여기서 refreshToken이 null이거나 validateToken 메서드가 false를 반환하면, filterChain.doFilter(request, response)를 호출하게 되어 다음 필터로 넘어갑니다. 즉, 인증이 이루어지지 않은 상태로 요청이 처리되며, 이후 Security 설정에 따라 접근이 제한됩니다.
        filterChain.doFilter(request, response);
    }

    private void authenticateWithToken(String token, byte[] secretKey, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(token, secretKey);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        log.info("getTokenFromRequest: {}", bearerToken);
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        // Refresh Token 을 어디서 가져올지는 결정해야 합니다.
        // 예를 들어, 쿠키에서 가져올 수도 있습니다.
        return request.getHeader("refresh-token");
    }
}