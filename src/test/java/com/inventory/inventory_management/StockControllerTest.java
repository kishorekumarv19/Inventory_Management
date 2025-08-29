package com.inventory.inventory_management;

import com.inventory.inventory_management.controller.StockController;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit tests for StockController using JUnit and Mockito
 */
@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(StockControllerTest.class);

    @Mock
    private ProductService productService;

    @InjectMocks
    private StockController stockController;

    /**
     * Test case for handling internal server error during stock addition.
     */
    @Test
    void addStock() {
        Long productId = 1L;
        int quantity = 10;

        // Simulate exception thrown by service
        when(productService.addStock(productId, quantity)).thenThrow(new RuntimeException("Database error"));
        logger.info("Testing addStock() with simulated service failure");

        ResponseEntity<Response> responseEntity = stockController.addStock(productId, quantity);

        // Assert the error response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("Internal Server Error", responseEntity.getBody().getMessage());
        assertEquals("An error occurred while adding stock: Database error", responseEntity.getBody().getData());

        logger.info("addStock() error handling passed");
    }

    /**
     * Test case for handling internal server error during stock removal.
     */
    @Test
    void removeStock() {
        Long productId = 1L;
        int quantity = 5;

        when(productService.removeStock(productId, quantity)).thenThrow(new RuntimeException("Database error"));
        logger.info("Testing removeStock() with simulated service failure");

        ResponseEntity<Response> responseEntity = stockController.removeStock(productId, quantity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("Internal Server Error", responseEntity.getBody().getMessage());
        assertEquals("An error occurred while removing stock: Database error", responseEntity.getBody().getData());

        logger.info("removeStock() error handling passed");
    }

    /**
     * Test case for handling internal server error while retrieving stock level.
     */
    @Test
    void getStockLevel() {
        Long productId = 1L;

        when(productService.getStockLevel(productId)).thenThrow(new RuntimeException("Database error"));
        logger.info("Testing getStockLevel() with simulated service failure");

        ResponseEntity<Response> responseEntity = stockController.getStockLevel(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("Internal Server Error", responseEntity.getBody().getMessage());
        assertEquals("An error occurred while retrieving stock level: Database error", responseEntity.getBody().getData());

        logger.info("getStockLevel() error handling passed");
    }

    /**
     * Test case for when the product is not found during stock addition.
     */
    @Test
    void addStock_ProductNotFound_ReturnsErrorResponse() {
        Long productId = 1L;
        int quantity = 10;

        when(productService.addStock(productId, quantity)).thenThrow(new RuntimeException("Product not found"));
        logger.info("Testing addStock() with non-existent product");

        ResponseEntity<Response> responseEntity = stockController.addStock(productId, quantity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("Internal Server Error", responseEntity.getBody().getMessage());
        assertEquals("An error occurred while adding stock: Product not found", responseEntity.getBody().getData());

        logger.info("addStock() with missing product handled correctly");
    }

    /**
     * Test case for when the product is not found during stock removal.
     */
    @Test
    void removeStock_ProductNotFound_ReturnsErrorResponse() {
        Long productId = 1L;
        int quantity = 5;

        when(productService.removeStock(productId, quantity)).thenThrow(new RuntimeException("Product not found"));
        logger.info("Testing removeStock() with non-existent product");

        ResponseEntity<Response> responseEntity = stockController.removeStock(productId, quantity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("Internal Server Error", responseEntity.getBody().getMessage());
        assertEquals("An error occurred while removing stock: Product not found", responseEntity.getBody().getData());

        logger.info("removeStock() with missing product handled correctly");
    }

    /**
     * Test case for when the product is not found during stock level retrieval.
     */
    @Test
    void getStockLevel_ProductNotFound_ReturnsErrorResponse() {
        Long productId = 1L;

        when(productService.getStockLevel(productId)).thenThrow(new RuntimeException("Product not found"));
        logger.info("Testing getStockLevel() with non-existent product");

        ResponseEntity<Response> responseEntity = stockController.getStockLevel(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("Internal Server Error", responseEntity.getBody().getMessage());
        assertEquals("An error occurred while retrieving stock level: Product not found", responseEntity.getBody().getData());

        logger.info("getStockLevel() with missing product handled correctly");
    }
}
