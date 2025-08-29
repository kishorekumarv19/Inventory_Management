package com.inventory.inventory_management.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model class representing an Order.
 * This class is used to transfer order data between layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    // Logger to log information, warnings, and errors
    private static final Logger logger = LogManager.getLogger(Order.class);

    /**
     * The ID of the product associated with the order.
     */
    private Long productId;

    /**
     * The quantity of the product ordered.
     * Must be at least 1.
     */
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    /**
     * The total price of the order.
     * Cannot be null.
     */
    @NotNull(message = "Price cannot be null")
    private double totalPrice;

    /**
     * The status of the order (e.g., "Pending", "Completed", "Cancelled").
     * Cannot be blank.
     */

    @Pattern(regexp = "PROCESSING|SHIPPED|CANCELED", message = "Status must be one of the following: PROCESSING, SHIPPED, CANCELED")
    private String status;

    /**
     * The user who placed the order.
     * Cannot be blank.
     */
    @NotBlank(message = "User cannot be blank")
    private String user;

    /**
     * Logs the details of the order.
     */
    public void logOrderDetails() {
        logger.info("Order Details - Product ID: {}, Quantity: {}, Total Price: {}, Status: {}, User: {}",
                productId, quantity, totalPrice, status, user);
    }
}