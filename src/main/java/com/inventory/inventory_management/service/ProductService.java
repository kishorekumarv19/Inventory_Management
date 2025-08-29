package com.inventory.inventory_management.service;

import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.repository.ProductRepository;
import com.inventory.inventory_management.repository.UserRepository;
import com.inventory.inventory_management.util.StockMovement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.List;

/**
 * Service class for managing products.
 */
@Service
public class ProductService {

    // Logger instance for logging service operations
    private static final Logger logger = LogManager.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    /**
     * Retrieves the entire inventory of products.
     *
     * @return a list of all products
     */
    public List<Product> getInventory() {
        try {
            List<Product> inventory = productRepository.findAll();
            if (inventory.isEmpty()) {
                throw new RuntimeException("No data in inventory");
            }
            return inventory;
        } catch (Exception ex) {
            logger.error("An error occurred while fetching inventory: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Retrieves a specific product from the inventory by its ID.
     *
     * @param id the ID of the product
     * @return the product with the specified ID, or null if not found
     */
    public Product getParticularInventory(Long id) {
        try {
            return productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching particular inventory: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Adds a new product to the inventory.
     *
     * @param product the product to be added
     * @return the added product
     */


    public Product addInventory(com.inventory.inventory_management.model.Product product) {
        try {
            // Check if the user exists
            userRepository.findByUsername(product.getUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if a product with the same name and description already exists
            if (productRepository.findByNameAndDescription(product.getName(), product.getDescription()).isPresent()) {
                throw new RuntimeException("A product with the same name and description already exists");
            }

            // Save the new product if no duplicate is found
            return productRepository.save(Product.builder()
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .createdBy(product.getUser())
                    .build());
        } catch (Exception ex) {
            logger.error("An error occurred while adding inventory: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Updates an existing product in the inventory.
     *
     * @param id the ID of the product to be updated
     * @param quantity the updated quantity details
     * @return a response indicating the result of the update operation
     */
    public Response updateInventory(Long id, Double price, Integer quantity, String user) {
        try {
            // Validate that user is not null or empty
            if (user == null || user.trim().isEmpty()) {
                return new Response("Error", "User is required", "User cannot be null or empty");
            }

            // Check if the user exists in the user table
            if (!userRepository.findByUsername(user).isPresent()) {
                return new Response("Error", "User not found", "The user does not exist");
            }

            // Retrieve the existing product from the database
            Product existingProduct = productRepository.findById(id).orElse(null);

            if (existingProduct != null) {
                // Update only the price if provided
                if (price != null) {
                    if (price <= 0) {
                        return new Response("Error", "Invalid price value", "Price must be greater than 0");
                    }
                    existingProduct.setPrice(price);
                }

                // Update only the quantity if provided
                if (quantity != null) {
                    if (quantity < 0) {
                        return new Response("Error", "Invalid quantity value", "Quantity cannot be negative");
                    }
                    existingProduct.setQuantity(quantity);
                }

                // Update the common fields
                existingProduct.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                existingProduct.setUpdatedBy(user);

                // Save the updated product back to the database
                Product updatedProduct = productRepository.save(existingProduct);

                // Return success response
                return new Response("success", "Inventory updated successfully", updatedProduct);
            } else {
                // Return error response if product not found
                logger.warn("Product not found for update, ID: {}", id);
                throw new RuntimeException("Product not found ");
            }
        } catch (Exception ex) {
            logger.error("An error occurred while updating inventory: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error updating product with ID: " + id, ex);
        }
    }

    /**
     * Deletes a product from the inventory by its ID.
     *
     * @param id the ID of the product to be deleted
     */
    public void deleteInventory(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception ex) {
            logger.error("An error occurred while deleting inventory: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Adds stock to an existing product in the inventory.
     *
     * @param productId the ID of the product
     * @param quantity the quantity to be added
     * @return the updated stock response
     */
    public StockMovement addStock(Long productId, int quantity) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setQuantity(product.getQuantity() + quantity);
            productRepository.save(product);
            return new StockMovement(productId.toString(), String.valueOf(product.getQuantity()));
        } catch (Exception ex) {
            logger.error("An error occurred while adding stock: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Removes stock from an existing product in the inventory.
     *
     * @param productId the ID of the product
     * @param quantity the quantity to be removed
     * @return the updated stock response
     */
    public StockMovement removeStock(Long productId, int quantity) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            return new StockMovement(productId.toString(), String.valueOf(product.getQuantity()));
        } catch (Exception ex) {
            logger.error("An error occurred while removing stock: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Retrieves the stock level of a specific product by its ID.
     *
     * @param productId the ID of the product
     * @return the stock response containing the product ID and stock level
     */
    public StockMovement getStockLevel(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            return new StockMovement(productId.toString(), String.valueOf(product.getQuantity()));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching stock level: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
    /**
     * Retrieves the stock level of a specific product by its name.
     *
     * @param productName the name of the product
     * @return the stock response containing the product ID and stock level
     */

}