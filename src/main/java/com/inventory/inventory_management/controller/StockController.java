package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.ProductService;
import com.inventory.inventory_management.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing stock operations.
 * Provides endpoints for adding, removing, and retrieving stock levels for products.
 */
@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private ProductService productService;

    // Logger to log info, error, and debug messages for the application
    private static final Logger logger = LogManager.getLogger(StockController.class);

    /**
     * Adds stock to a product.
     *
     * @param productId the ID of the product to which stock will be added
     * @param quantity  the quantity of stock to add
     * @return a response entity containing the result of the operation or an error message
     */
    @PostMapping("/add")
    public ResponseEntity<Response> addStock(@RequestParam Long productId, @RequestParam int quantity) {
        logger.info("Adding stock: productId={}, quantity={}", productId, quantity);
        try {
            // Validate quantity
            if (quantity < 1) {
                logger.warn("Invalid quantity: {}", quantity);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(Constants.ERROR, "Invalid quantity", "Quantity must be greater than or equal to 1"));
            }

            // Call the service to add stock
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Stock added successfully", productService.addStock(productId, quantity)));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("An error occurred while adding stock: productId={}, quantity={}", productId, quantity, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while adding stock: " + ex.getMessage()));
        }
    }

    /**
     * Removes stock from a product.
     *
     * @param productId the ID of the product from which stock will be removed
     * @param quantity  the quantity of stock to remove
     * @return a response entity containing the result of the operation or an error message
     */
    @PostMapping("/remove")
    public ResponseEntity<Response> removeStock(@RequestParam Long productId, @RequestParam int quantity) {
        logger.info("Removing stock: productId={}, quantity={}", productId, quantity);
        try {
            // Validate quantity
            if (quantity < 1) {
                logger.warn("Invalid quantity: {}", quantity);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(Constants.ERROR, "Invalid quantity", "Quantity must be greater than or equal to 1"));
            }

            // Call the service to remove stock
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Stock removed successfully", productService.removeStock(productId, quantity)));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("An error occurred while removing stock: productId={}, quantity={}", productId, quantity, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while removing stock: " + ex.getMessage()));
        }
    }
    /**
     * Retrieves the stock level of a product.
     *
     * @param id the ID of the product whose stock level will be retrieved
     * @return a response entity containing the stock level or an error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getStockLevel(@PathVariable Long id) {
        logger.info("Retrieving stock level for productId={}", id);
        try {
            // Call the service to get the stock level
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Stock level retrieved successfully", productService.getStockLevel(id)));
        } catch (Exception ex) {
            // Log and return error response
            logger.error("An error occurred while retrieving stock level for productId={}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while retrieving stock level: " + ex.getMessage()));
        }
    }
}