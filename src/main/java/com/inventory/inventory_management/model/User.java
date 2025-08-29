package com.inventory.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entity class representing a User.
 * This class maps to the `UserModel` table in the database and contains details about users in the system.
 */
@Entity(name = "UserModel")
@Data
public class User {

    // Logger to log information, warnings, and errors
    private static final Logger logger = LogManager.getLogger(User.class);

    /**
     * Unique identifier for the user.
     * This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username of the user.
     * Must be between 3 and 20 characters and cannot be blank.
     */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * The password of the user.
     * Cannot be blank.
     */
    @NotBlank(message = "Password is mandatory")
    @Column(nullable = false)
    private String password;

    /**
     * The role of the user (e.g., "ADMIN" or "USER").
     * Must match the pattern "ADMIN" or "USER" and cannot be blank.
     */
    @Column(nullable = false)
    @NotBlank(message = "Role is mandatory")
    @Pattern(regexp = "ADMIN|USER", message = "Role must be either ADMIN or USER")
    private String role;

    /**
     * Logs the details of the user.
     * Note: Avoid logging sensitive information like passwords in production.
     */
    public void logUserDetails() {
        logger.info("User Details - ID: {}, Username: {}, Role: {}", id, username, role);
    }
}