package com.inventory.inventory_management.repository;

import com.inventory.inventory_management.entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Order entities.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Logger instance for logging repository operations
    Logger logger = LoggerFactory.getLogger(OrderRepository.class);
    /**
     * Logs the entry into the OrderRepository.
     * This method can be used to log repository operations.
     */
    default void logRepositoryEntry() {
        logger.info("Entered OrderRepository");}


}