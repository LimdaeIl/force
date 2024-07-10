package com.dedication.force.common.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.mockito.Mockito.*;

@DisplayName("[JWT] JwtAuthenticationFilter")
@ActiveProfiles("test")
class JwtAuthenticationFilterTest  {


    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService customUserDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("유효한 액세스 토큰을 사용하여 필터가 올바르게 동작하는지 확인")
    @Test
    void testDoFilterInternal_withValidAccessToken() throws ServletException, IOException {
        // Set up the mock request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validAccessToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        // Mock the JwtTokenProvider methods
        byte[] secretKey = "secretKey".getBytes();
        when(jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY()).thenReturn(secretKey);
        when(jwtTokenProvider.validateToken("validAccessToken", secretKey)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("validAccessToken", secretKey)).thenReturn("test@naver.com");

        // Mock the UserDetailsService method
        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("test@naver.com")).thenReturn(userDetails);

        // Call the filter method
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that validateToken was called
        verify(jwtTokenProvider, times(1)).validateToken("validAccessToken", secretKey);
        verify(jwtTokenProvider, times(1)).getEmailFromToken("validAccessToken", secretKey);
        verify(customUserDetailsService, times(1)).loadUserByUsername("test@naver.com");
    }
}
