package com.inventory.inventory_management.service;

import com.inventory.inventory_management.config.JwtUtil;
import com.inventory.inventory_management.controller.OrderController;
import com.inventory.inventory_management.repository.UserRepository;
import com.inventory.inventory_management.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    /**
     * Loads the user by username.
     *
     * @param username the username of the user
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Log the attempt to load user by username
            logger.info("Loading user by username: {}", username);

            // Fetch user from database
            Optional<User> userEntityOptional = userRepository.findByUsername(username);

            // If user is not found, throw a custom exception
            if (!userEntityOptional.isPresent()) {
                logger.warn("User not found with username: {}", username);
                throw new UserNotFoundException("User not found with username: " + username);
            }

            User userEntity = userEntityOptional.get();

            // Return user details
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userEntity.getUsername())
                    .password(userEntity.getPassword())
                    .roles(userEntity.getRole())
                    .build();
        } catch (Exception ex) {
            // Log the exception
            logger.error("An error occurred while loading user by username: {}", username, ex);
            throw ex;
        }
    }

    /**
     * Custom exception for user not found scenario.
     */
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Saves a new user to the repository.
     *
     * @param user the user to save
     * @return the saved user
     */
    public User registerUser(User user) {
        return userRepository.save(user);
    }








    public Map<String, String> login(User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        logger.info("Attempting to log in user: {}", user.getUsername());

        if (optionalUser.isPresent()) {
            User storedUser = optionalUser.get();

            logger.info("User found: {}, verifying password.", storedUser.getUsername());

            if (!storedUser.getPassword().startsWith("$2a$")) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                storedUser.setPassword(encodedPassword);
                userRepository.save(storedUser);
                logger.info("Password was not encoded. Re-encoded and saved for user: {}", user.getUsername());
            }

            if (passwordEncoder.matches(user.getPassword(), storedUser.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());
                return Map.of("message", "Login successful", "token", token);
            } else {
                logger.warn("Password mismatch for user: {}", user.getUsername());
                throw new RuntimeException("Invalid username or password");
            }
        } else {
            logger.warn("User not found: {}", user.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
    }

}