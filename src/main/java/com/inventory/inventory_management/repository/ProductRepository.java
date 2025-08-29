package com.inventory.inventory_management.repository;

import com.inventory.inventory_management.entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Product entities.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Logger instance for logging repository operations
    Logger logger = LoggerFactory.getLogger(ProductRepository.class);
    default void logRepositoryEntry() {
        logger.info("Entered ProductRepository");}

    /**
     * Finds a product by its description.
     *
     * @param description the description of the product
     * @return an Optional containing the product if found, or empty otherwise
     */
    Optional<Product> findByNameAndDescription(String name, String description);
}