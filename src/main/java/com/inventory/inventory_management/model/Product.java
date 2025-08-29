package com.inventory.inventory_management.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model class representing a Product.
 * This class is used to transfer product data between layers of the application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    // Logger to log information, warnings, and errors
    private static final Logger logger = LogManager.getLogger(Product.class);

    /**
     * Unique identifier for the product.
     */
    private String id;

    /**
     * The name of the product.
     * Cannot be null or blank.
     */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    /**
     * The description of the product.
     * Cannot be null or blank.
     */
    @NotBlank(message = "Description cannot be blank")
    private String description;

    /**
     * The price of the product.
     * Must be greater than zero and cannot be null.
     */
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    /**
     * The quantity of the product in stock.
     * Must be zero or greater and cannot be null.
     */
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be zero or greater")
    private Integer quantity;

    /**
     * The user who created or updated the product.
     * Cannot be null or blank.
     */
    @NotBlank(message = "User cannot be blank")
    private String user;

    /**
     * Logs the details of the product.
     */
    public void logProductDetails() {
        logger.info("Product Details - ID: {}, Name: {}, Description: {}, Price: {}, Quantity: {}, User: {}",
                id, name, description, price, quantity, user);
    }
}