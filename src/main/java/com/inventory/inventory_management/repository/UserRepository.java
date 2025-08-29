package com.inventory.inventory_management.repository;

import com.inventory.inventory_management.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Logger instance for logging repository operations
    Logger logger = LoggerFactory.getLogger(UserRepository.class);
    default void logRepositoryEntry() {
        logger.info("Entered UserRepository");}
    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
}