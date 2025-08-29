package com.inventory.inventory_management.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity class representing a User.
 */
@Data
@Entity(name = "UserEntity")
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username of the user.
     */
    private String username;

    /**
     * Password of the user.
     */
    private String password;

    /**
     * Role of the user.
     */
    private String role;
}