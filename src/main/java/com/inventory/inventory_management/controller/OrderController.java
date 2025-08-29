package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.entities.Order;
import com.inventory.inventory_management.service.OrderService;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.util.Constants;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing orders.
 * Provides endpoints for creating, retrieving, updating, and canceling orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    /**
     * Creates a new order.
     *
     * @param order the order to create
     * @return a response entity with the created order or an error message
     */
    @PostMapping("/add")
    public ResponseEntity<Response> createOrder(@Valid @RequestBody com.inventory.inventory_management.model.Order order) {
        logger.info("Creating new order: {}", order);
        try {
            Order createdOrder = orderService.createOrder(order);
            if (createdOrder != null) {
                logger.info("Order created successfully: {}", createdOrder);
                return ResponseEntity.ok(new Response(Constants.SUCCESS, "Order created successfully", createdOrder));
            }
            logger.warn("Order creation failed for order: {}", order);
            return ResponseEntity.badRequest().body(new Response(Constants.ERROR, "Order creation failed", "Invalid Order"));
        } catch (Exception ex) {
            logger.error("An error occurred while creating the order: {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(new Response(Constants.ERROR, "Order creation failed", ex.getMessage()));
        }
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return a response entity with the order or an error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOrderById(@PathVariable Long id) {
        logger.info("Fetching order with ID: {}", id);
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                logger.info("Order fetched successfully: {}", order);
                return ResponseEntity.ok(new Response(Constants.SUCCESS, "Order fetched successfully", order));
            }
            logger.warn("Order not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Constants.ERROR, "Order not found", "Order not found"));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching the order with ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while fetching the order: " + ex.getMessage()));
        }
    }

    /**
     * Retrieves all orders.
     *
     * @return a response entity with the list of orders or an error message
     */
    @GetMapping
    public ResponseEntity<Response> getAllOrders() {
        logger.info("Fetching all orders");
        try {
            List<Order> orders = orderService.getAllOrders();
            if (orders.isEmpty()) {
                logger.warn("No data found in the orders table");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(Constants.ERROR, "No data in database", "The orders table is empty"));
            }
            logger.info("Orders fetched successfully: {}", orders);
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Orders fetched successfully", orders));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching all orders", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while fetching orders: " + ex.getMessage()));
        }
    }

    /**
     * Updates the status of an order.
     *
     * @param id the ID of the order to update
     * @param status the new status of the order
     * @param user the user performing the update
     * @return a response entity with the updated order or an error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateOrderStatus(@PathVariable Long id, @RequestParam String status, @RequestParam String user) {
        logger.info("Updating order status for order ID: {} to status: {} by user: {}", id, status, user);
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status, user);
            if (updatedOrder != null) {
                logger.info("Order status updated successfully: {}", updatedOrder);
                return ResponseEntity.ok(new Response(Constants.SUCCESS, "Order status updated successfully", updatedOrder));
            }
            logger.warn("Order update failed for order ID: {}", id);
            return ResponseEntity.badRequest().body(new Response(Constants.ERROR, "Order update failed", "Invalid Order"));
        } catch (Exception ex) {
            logger.error("An error occurred while updating the order with ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while updating the order: " + ex.getMessage()));
        }
    }

    /**
     * Cancels an order.
     *
     * @param id the ID of the order to cancel
     * @return a response entity with a success or error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> cancelOrder(@PathVariable Long id) {
        logger.info("Canceling order with ID: {}", id);
        try {
            return orderService.cancelOrder(id);
        } catch (Exception ex) {
            logger.error("An error occurred while canceling the order with ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "An error occurred while canceling the order: " + ex.getMessage(), "Order cancellation failed"));
        }
    }
}