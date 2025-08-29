package com.inventory.inventory_management;

import com.inventory.inventory_management.config.JwtUtil;
import com.inventory.inventory_management.model.User;
import com.inventory.inventory_management.repository.UserRepository;
import com.inventory.inventory_management.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    // Logger for the test class
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(CustomUserDetailsServiceTest.class);

    // Mocked dependencies
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    // Service to be tested
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        logger.info("Setting up the mocks for the test...");
        MockitoAnnotations.openMocks(this);
        logger.info("Mocks set up successfully.");
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Test case for a user that exists in the repository
        logger.info("Testing loadUserByUsername for an existing user...");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole("USER");

        // Mocking the repository response for an existing user
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Call the method to test
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Asserting the returned user details
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());

        logger.info("loadUserByUsername test passed for an existing user.");
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUserNotFoundException() {
        // Test case for a user that does not exist in the repository
        logger.info("Testing loadUserByUsername for a non-existing user...");

        // Mocking the repository to return an empty Optional for a nonexistent user
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Assert that a UserNotFoundException is thrown
        assertThrows(CustomUserDetailsService.UserNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistentuser");
        });

        logger.info("loadUserByUsername test passed for a non-existing user (exception thrown).");
    }

    @Test
    void registerUser_ValidUser_ReturnsSavedUser() {
        // Test case for user registration with valid input
        logger.info("Testing registerUser with valid user...");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        // Mocking repository to return the user when saved
        when(userRepository.save(user)).thenReturn(user);

        // Calling the register method to test
        User savedUser = customUserDetailsService.registerUser(user);

        // Asserting the saved user
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());

        // Verifying that save was called exactly once
        verify(userRepository, times(1)).save(user);

        logger.info("registerUser test passed for valid user registration.");
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        // Test case for login with valid credentials
        logger.info("Testing login with valid credentials...");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        // Creating a stored user object with an encoded password
        User storedUser = new User();
        storedUser.setUsername("testuser");
        storedUser.setPassword("$2a$10$encodedpassword");

        // Mocking the repository to return the stored user and password check
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("password", "$2a$10$encodedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("mockedToken");

        // Calling the login method to test
        Map<String, String> response = customUserDetailsService.login(user);

        // Asserting the response contains the expected values
        assertNotNull(response);
        assertEquals("Login successful", response.get("message"));
        assertEquals("mockedToken", response.get("token"));

        // Verifying the repository and JWT generation calls
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(jwtUtil, times(1)).generateToken("testuser");

        logger.info("login test passed for valid credentials.");
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // Test case for login with invalid password
        logger.info("Testing login with invalid password...");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("wrongpassword");

        // Creating a stored user object with an encoded password
        User storedUser = new User();
        storedUser.setUsername("testuser");
        storedUser.setPassword("$2a$10$encodedpassword");

        // Mocking the repository and password check
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedpassword")).thenReturn(false);

        // Asserting that an exception is thrown when passwords do not match
        RuntimeException exception = assertThrows(RuntimeException.class, () -> customUserDetailsService.login(user));
        assertEquals("Invalid username or password", exception.getMessage());

        // Verifying the repository call
        verify(userRepository, times(1)).findByUsername("testuser");

        logger.info("login test passed for invalid password.");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Test case for login when user is not found
        logger.info("Testing login when user is not found...");

        User user = new User();
        user.setUsername("nonexistentuser");
        user.setPassword("password");

        // Mocking the repository to return an empty Optional for a nonexistent user
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Asserting that an exception is thrown when the user is not found
        RuntimeException exception = assertThrows(RuntimeException.class, () -> customUserDetailsService.login(user));
        assertEquals("Invalid username or password", exception.getMessage());

        // Verifying the repository call
        verify(userRepository, times(1)).findByUsername("nonexistentuser");

        logger.info("login test passed for non-existent user (exception thrown).");
    }
}
