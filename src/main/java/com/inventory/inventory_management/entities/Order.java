package com.inventory.inventory_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Entity class representing an Order.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name="orders")
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The product associated with the order.
     */
    @ManyToOne
    private Product productId;

    /**
     * The quantity of the product ordered.
     */
    private int quantity;

    /**
     * The total price of the order.
     */
    private double totalPrice;

    /**
     * The status of the order.
     */
    @Column(name="status")
    private String status;

    /**
     * The timestamp when the order was created.
     */
    @Column(name="created_at")
    private Timestamp createdAt;

    /**
     * The timestamp when the order was last updated.
     */
    @Column(name="updated_at")
    private Timestamp updatedAt;

    /**
     * The user who created the order.
     */
    @Column(name="created_by")
    private String createdBy;

    /**
     * The user who last updated the order.
     */
    @Column(name="updated_by")
    private String updatedBy;
}