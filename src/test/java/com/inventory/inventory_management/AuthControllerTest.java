package com.inventory.inventory_management;

import com.inventory.inventory_management.controller.AuthController;
import com.inventory.inventory_management.model.User;
import com.inventory.inventory_management.repository.UserRepository;
import com.inventory.inventory_management.config.JwtUtil;
import com.inventory.inventory_management.service.CustomUserDetailsService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthControllerTest {

    // Logger for the test class
    private static final Logger logger = LogManager.getLogger(AuthControllerTest.class);

    private AuthController authController;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private CustomUserDetailsService customUserDetailsService;

    @Before
    public void setUp() throws Exception {
        // Setting up mocks for all dependencies
        logger.info("Setting up mocks and initializing AuthController...");

        // Mocking dependencies
        userRepository = Mockito.mock(UserRepository.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        customUserDetailsService = Mockito.mock(CustomUserDetailsService.class);

        // Initializing the AuthController
        authController = new AuthController();

        // Using reflection to set the private fields of AuthController with mocked dependencies
        setPrivateField(authController, "userRepository", userRepository);
        setPrivateField(authController, "jwtUtil", jwtUtil);
        setPrivateField(authController, "passwordEncoder", passwordEncoder);
        setPrivateField(authController, "customUserDetailsService", customUserDetailsService);

        logger.info("Mocks and AuthController initialized successfully.");
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        // Setting the private field of the AuthController using reflection
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void register_UserSuccessfullyRegistered() {
        // Test case for successful user registration
        logger.info("Testing user registration with a new user...");

        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newPassword");

        // Mocking behavior when the username is not already taken
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());

        // Calling the register method of AuthController
        ResponseEntity<String> response = authController.register(newUser);

        // Verifying the interaction and asserting the expected result
        Mockito.verify(customUserDetailsService).registerUser(newUser);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertEquals("User registered successfully!", response.getBody());

        logger.info("User registration test passed for a new user.");
    }

    @Test
    public void register_UsernameAlreadyExists() {
        // Test case for when the username already exists
        logger.info("Testing user registration with an existing username...");

        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setPassword("password");

        // Mocking behavior when the username already exists
        Mockito.when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));

        // Calling the register method of AuthController
        ResponseEntity<String> response = authController.register(existingUser);

        // Asserting that the response indicates a conflict due to the existing username
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Username already exists!", response.getBody());

        logger.info("User registration test passed for an existing username.");
    }

    @Test
    public void register_InternalServerError() {
        // Test case for an internal server error scenario during registration
        logger.info("Testing user registration with an internal server error...");

        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newPassword");

        // Mocking behavior for successful username check and then throwing an exception in the registerUser method
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        Mockito.doThrow(new RuntimeException("Database error"))
                .when(customUserDetailsService).registerUser(newUser);

        // Calling the register method of AuthController
        ResponseEntity<String> response = authController.register(newUser);

        // Asserting that the response indicates an internal server error
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("An error occurred while registering the user"));

        logger.info("User registration test passed for internal server error scenario.");
    }

    @Test
    public void login_SuccessfulLogin() {
        // Test case for successful login
        logger.info("Testing successful user login...");

        User validUser = new User();
        validUser.setUsername("validUser");
        validUser.setPassword("validPassword");

        // Mocking a successful login response with a token
        Map<String, String> mockResponse = Map.of("token", "mockToken");

        Mockito.when(customUserDetailsService.login(validUser)).thenReturn(mockResponse);

        // Calling the login method of AuthController
        ResponseEntity<Map<String, String>> response = authController.login(validUser);

        // Verifying the response contains the expected token
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("mockToken", response.getBody().get("token"));

        logger.info("User login test passed for successful login.");
    }

    @Test
    public void login_InvalidCredentials() {
        // Test case for login failure due to invalid credentials
        logger.info("Testing user login with invalid credentials...");

        User invalidUser = new User();
        invalidUser.setUsername("user");
        invalidUser.setPassword("wrong");

        // Mocking the login to throw an exception for invalid credentials
        Mockito.when(customUserDetailsService.login(invalidUser))
                .thenThrow(new RuntimeException("Invalid username or password"));

        // Calling the login method of AuthController
        ResponseEntity<Map<String, String>> response = authController.login(invalidUser);

        // Asserting that the response indicates unauthorized access
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assert.assertEquals("Unauthorized", response.getBody().get("error"));
        Assert.assertEquals("Invalid username or password", response.getBody().get("message"));

        logger.info("User login test passed for invalid credentials.");
    }

    @Test
    public void login_InternalServerError() {
        // Test case for login failure due to an internal server error
        logger.info("Testing user login with an internal server error...");

        User validUser = new User();
        validUser.setUsername("validUser");
        validUser.setPassword("validPassword");

        // Mocking the login to throw an exception for an unexpected error
        Mockito.when(customUserDetailsService.login(validUser))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Calling the login method of AuthController
        ResponseEntity<Map<String, String>> response = authController.login(validUser);

        // Asserting that the response indicates unauthorized access with the error message
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assert.assertEquals("Unauthorized", response.getBody().get("error"));
        Assert.assertEquals("Unexpected error", response.getBody().get("message"));

        logger.info("User login test passed for internal server error scenario.");
    }
}
