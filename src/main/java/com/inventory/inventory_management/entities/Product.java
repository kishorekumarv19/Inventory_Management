package com.inventory.inventory_management.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Entity class representing a Product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Product {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product.
     */
    @NotBlank
    private String name;

    /**
     * Description of the product.
     */
    private String description;

    /**
     * Quantity of the product in stock.
     */
    @Min(0)
    @Column(name="quantity_in_stock")
    private int quantity;

    /**
     * Price of the product.
     */
    @Min(1)
    private double price;

    /**
     * User who created the product.
     */
    @Column(name="created_by")
    private String createdBy;

    /**
     * Timestamp when the product was created.
     */
    @Column(name="created_at")
    private Timestamp createdAt;

    /**
     * User who last updated the product.
     */
    @Column(name="updated_by")
    private String updatedBy;

    /**
     * Timestamp when the product was last updated.
     */
    @Column(name="updated_at")
    private Timestamp updatedAt;
}