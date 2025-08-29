package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.config.JwtUtil;
import com.inventory.inventory_management.model.User;
import com.inventory.inventory_management.repository.UserRepository;

import com.inventory.inventory_management.service.CustomUserDetailsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @return a response entity with a success or error message
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            // Check if the username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username already exists!");
            }

            // Save the new user to the repository
            customUserDetailsService.registerUser(user);

            // Return success message
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully!");

        } catch (Exception ex) {
            // Catch any unexpected exceptions and return a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the user: " + ex.getMessage());
        }
    }

    /**
     * Logs in a user.
     *
     * @param user the user to log in
     * @return a response entity with a success message and token or an error message
     */

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        try {
            Map<String, String> response = customUserDetailsService.login(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("An error occurred during login: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("An unexpected error occurred during login", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An error occurred during login"));
        }
    }
    
}