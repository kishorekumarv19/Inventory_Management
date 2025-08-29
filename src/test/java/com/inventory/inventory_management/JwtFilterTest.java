package com.inventory.inventory_management;

import com.inventory.inventory_management.config.JwtFilter;
import com.inventory.inventory_management.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.*;

import java.io.IOException;

class JwtFilterTest {

    // Logger for the test class
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(JwtFilterTest.class);

    // Mocked dependencies
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        logger.info("Setting up the mocks for the JwtFilter test...");
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext); // set the security context
        logger.info("Mocks set up successfully and security context initialized.");
    }

    // Test case for a valid token
    @Test
    void testDoFilterInternal_TokenPresentAndValid() throws IOException, ServletException {
        logger.info("Testing valid token scenario...");

        String token = "validToken";
        String username = "testUser";
        String authHeader = "Bearer " + token;

        // Mocking JwtUtil
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);

        UserDetails userDetails = User.builder().username(username).password("password").roles("USER").build();
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Call the filter method
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Verify authentication was set in the security context
        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);

        logger.info("Valid token test passed. Authentication set and filter chain proceeded.");
    }

    // Test case for an invalid token
    @Test
    void testDoFilterInternal_TokenInvalid() throws IOException, ServletException {
        logger.info("Testing invalid token scenario...");

        String token = "invalidToken";
        String authHeader = "Bearer " + token;

        // Mocking JwtUtil
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Call the filter method
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Verify that authentication is not set in the security context
        verify(securityContext, never()).setAuthentication(any());

        // Verify that an unauthorized error was sent
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "INVALID OR EXPIRED TOKEN");

        // Verify that filter chain doesn't continue after an error
        verify(filterChain, never()).doFilter(request, response);

        logger.info("Invalid token test passed. Unauthorized error sent and filter chain stopped.");
    }

    // Test case for missing Authorization header
    @Test
    void testDoFilterInternal_TokenNotPresent() throws IOException, ServletException {
        logger.info("Testing missing Authorization header scenario...");

        // Mocking JwtUtil
        when(request.getHeader("Authorization")).thenReturn(null);

        // Call the filter method
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Verify that authentication is not set in the security context
        verify(securityContext, never()).setAuthentication(any());

        // Verify that filter chain continues
        verify(filterChain).doFilter(request, response);

        logger.info("Missing Authorization header test passed. No authentication set and filter chain continued.");
    }

}
