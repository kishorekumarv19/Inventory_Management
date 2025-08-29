package com.inventory.inventory_management.service;

import com.inventory.inventory_management.model.Order;
import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.model.User;
import com.inventory.inventory_management.repository.OrderRepository;
import com.inventory.inventory_management.repository.ProductRepository;
import com.inventory.inventory_management.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing orders.
 */
@Service
public class OrderService {

    // Logger instance for logging service operations
    private static final Logger logger = LogManager.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Creates a new order.
     *
     * @param order the order to be created
     * @return the created order
     */
    @Transactional
    public com.inventory.inventory_management.entities.Order createOrder(Order order) throws RuntimeException {
        try {
            // Check if the user exists
            userRepository.findByUsername(order.getUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch the product from the database
            Optional<Product> productOpt = productRepository.findById(order.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                // Check if there is enough stock
                if (product.getQuantity() >= order.getQuantity()) {
                    // Sets the status to "PROCESSING" if not provided
                    if (order.getStatus() == null || order.getStatus().isBlank()) {
                        order.setStatus("PROCESSING");
                    }
                    // Create a new order entity
                    com.inventory.inventory_management.entities.Order newOrder = com.inventory.inventory_management.entities.Order.builder()
                            .productId(product)
                            .quantity(order.getQuantity())
                            .status(order.getStatus())
                            .createdAt(new Timestamp(System.currentTimeMillis()))
                            .createdBy(order.getUser())
                            .totalPrice(product.getPrice() * order.getQuantity())
                            .build();

                    // Save the order to the database
                    return orderRepository.save(newOrder);
                } else {
                    logger.error("Insufficient stock for product {}", order.getProductId());
                    throw new RuntimeException("Insufficient stock for the product");
                }
            } else {
                logger.error("Product not found with ID {}", order.getProductId());
                throw new RuntimeException("Product not found");
            }
        } finally {
            // Log the completion of the order creation process
            logger.info("Order creation process completed, see log for status");
        }
    }
    /**
     * Retrieves all orders.
     *
     * @return a list of all orders
     */
    public List<com.inventory.inventory_management.entities.Order> getAllOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception ex) {
            logger.error("An error occurred while fetching all orders: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error occurred while fetching all orders", ex);
        }
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order
     * @return the order with the specified ID
     */
    public com.inventory.inventory_management.entities.Order getOrderById(Long id) {
        try {
            return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching the order by ID: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Updates the status of an order.
     *
     * @param id the ID of the order
     * @param status the new status of the order
     * @param user the user updating the order
     * @return the updated order
     */
    public com.inventory.inventory_management.entities.Order updateOrderStatus(Long id, String status, String user) {
        try {
            List<String> statusValue = new ArrayList<>();
            statusValue.add("PROCESSING");
            statusValue.add("SHIPPED");
            statusValue.add("CANCELED");
            com.inventory.inventory_management.entities.Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
             String oldStatus = order.getStatus();
            User updatingUser = userRepository.findByUsername(user)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(order.getProductId().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if(statusValue.contains(status)) {
                if(status.equals("CANCELLED")&& oldStatus.equals("SHIPPED")){
                    order.setTotalPrice(order.getTotalPrice()-(order.getQuantity()*product.getPrice()));
                    product.setQuantity(product.getQuantity() + order.getQuantity());
                } else if (status.equals("SHIPPED")) {
                    product.setQuantity(product.getQuantity() - order.getQuantity());
                }
                productRepository.save(product);
                    order.setStatus(status);
                    order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    order.setUpdatedBy(user);
                return orderRepository.save(order);
                } else{
                    throw new RuntimeException("Invalid Status");
                }
        }
        catch (Exception ex) {
            logger.error("An error occurred while updating the order status: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Cancels an order by its ID.
     *
     * @param id the ID of the order to be canceled
     */

    public ResponseEntity<Response> cancelOrder(Long id) {
        try {
            if (!orderRepository.existsById(id)) {
                logger.warn("Order not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response("Error", "Order not found", "The specified order does not exist"));
            }
            orderRepository.deleteById(id);
            logger.info("Order successfully deleted with ID: {}", id);
            return ResponseEntity.ok(new Response("Success", "Order successfully deleted", null));
        } catch (Exception ex) {
            logger.error("An error occurred while canceling the order: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("Error", "Internal Server Error", "An error occurred while canceling the order: " + ex.getMessage()));
        }
    }
}