package com.inventory.inventory_management.controller;


import com.inventory.inventory_management.model.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.OrderService;
import com.inventory.inventory_management.service.ProductService;
import com.inventory.inventory_management.util.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing inventory operations.
 * Provides endpoints for CRUD operations on inventory items.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    private static final Logger logger = LogManager.getLogger(InventoryController.class);

    /**
     * Retrieves the entire inventory.
     *
     * @return ResponseEntity containing the inventory data or an error message.
     */
    @GetMapping
    public ResponseEntity<Response> getInventory() {
        logger.info("Fetching all inventory items");
        try {
            // Fetch inventory from the service
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "success", productService.getInventory()));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("Error occurred while fetching inventory: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while fetching inventory: " + ex.getMessage()));
        }
    }

    /**
     * Retrieves a specific inventory item by ID.
     *
     * @param id the ID of the inventory item to retrieve.
     * @return ResponseEntity containing the inventory item or an error message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getParticularInventory(@Valid @PathVariable(value = "id") @NotNull Long id) {
        logger.info("Fetching inventory item with ID: {}", id);
        try {
            // Fetch specific inventory item from the service
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "success", productService.getParticularInventory(id)));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("Error occurred while fetching inventory item with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while fetching inventory item: " + ex.getMessage()));
        }
    }

    /**
     * Adds a new inventory item.
     *
     * @param product the product to add to the inventory.
     * @return ResponseEntity containing the created inventory item or an error message.
     */
    @PostMapping("/add")
    public ResponseEntity<Response> addInventory(@Valid @RequestBody Product product) {
        logger.info("Adding new inventory item: {}", product.getName());
        try {
            // Add new inventory item using the service
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Inventory item created successfully", productService.addInventory(product)));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("Error occurred while adding inventory item: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.BAD, "Internal Server Error", "An error occurred while adding inventory item: " + ex.getMessage()));
        }
    }

    /**
     * Deletes an inventory item by ID.
     *
     * @param id the ID of the inventory item to delete.
     * @return ResponseEntity containing a success or error message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteInventory(@PathVariable(value = "id") Long id) {
        logger.info("Deleting inventory item with ID: {}", id);
        try {
            // Check if the inventory item exists
            if (null != productService.getParticularInventory(id)) {
                // Delete the inventory item
                productService.deleteInventory(id);
                logger.info("Successfully deleted inventory item with ID: {}", id);
                return ResponseEntity.ok(new Response(Constants.SUCCESS, "success", "Deleted Successfully"));
            }
            // Log and return invalid ID response
            logger.warn("Invalid inventory ID: {}", id);
            return ResponseEntity.badRequest().body(new Response(Constants.BAD, Constants.INVALID_INPUT, "Invalid Inventory ID"));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("Error occurred while deleting inventory item with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while deleting inventory item: " + ex.getMessage()));
        }
    }

    /**
     * Updates an inventory item by ID.
     *
     * @param id       the ID of the inventory item to update.
     * @param price    the new price of the inventory item (optional).
     * @param quantity the new quantity of the inventory item (optional).
     * @param user     the user performing the update (mandatory).
     * @return ResponseEntity containing the updated inventory item or an error message.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateInventory(
            @PathVariable Long id,
            @RequestParam(required = false) Double price,  // Optional price
            @RequestParam(required = false) Integer quantity,  // Optional quantity
            @RequestParam(required = true) String user) {  // Mandatory user
        logger.info("Updating inventory item with ID: {}", id);
        try {
            // Validate user input
            if (user == null || user.trim().isEmpty()) {
                logger.warn("User is required for updating inventory");
                return ResponseEntity.badRequest().body(new Response("Error", "User is required", "User cannot be null or empty"));
            }

            // Validate that at least one of price or quantity is provided
            if (price == null && quantity == null) {
                logger.warn("At least one of price or quantity must be provided for updating inventory");
                return ResponseEntity.badRequest().body(new Response("Error", "Invalid Input", "At least one of price or quantity must be provided"));
            }

            // Call service to update the inventory with params
            Response response = productService.updateInventory(id, price, quantity, user);

            if (response != null) {
                logger.info("Successfully updated inventory item with ID: {}", id);
                return ResponseEntity.ok(response); // Successful response
            }

            // If no product found or any error occurs
            logger.warn("Inventory update failed for ID: {}", id);
            return ResponseEntity.badRequest().body(new Response("Error", "Inventory update failed", "Invalid product ID"));

        } catch (Exception ex) {
            // Log and return error response
            logger.error("Error occurred while updating inventory item with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("Error", "Internal Server Error", "An error occurred while updating inventory item: " + ex.getMessage()));
        }
    }
}