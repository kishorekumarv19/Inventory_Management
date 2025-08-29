package com.inventory.inventory_management;

import com.inventory.inventory_management.controller.InventoryController;
import com.inventory.inventory_management.model.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.ProductService;
import com.inventory.inventory_management.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryControllerTest {

    // Logger for the test class
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(InventoryControllerTest.class);

    // Mocked dependencies
    @Mock
    private ProductService productService;

    // Injecting the mocked dependencies into the controller
    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        logger.info("Setting up the mocks for the test...");
        MockitoAnnotations.openMocks(this);
        logger.info("Mocks set up successfully.");
    }

    @Test
    void testGetInventory() {
        // Test case for getting the entire inventory
        logger.info("Testing getInventory method...");

        when(productService.getInventory()).thenReturn(List.of(new com.inventory.inventory_management.entities.Product()));

        // Calling the controller method
        ResponseEntity<Response> response = inventoryController.getInventory();

        // Asserting the response status and data
        assertEquals("success", response.getBody().getStatus());
        verify(productService, times(1)).getInventory();

        logger.info("getInventory test passed.");
    }

    @Test
    void testGetParticularInventory() {
        // Test case for fetching a specific product by ID
        logger.info("Testing getParticularInventory method...");

        com.inventory.inventory_management.entities.Product product = new com.inventory.inventory_management.entities.Product();
        when(productService.getParticularInventory(1L)).thenReturn(product);

        // Calling the controller method
        ResponseEntity<Response> response = inventoryController.getParticularInventory(1L);

        // Asserting the response status and data
        assertEquals("success", response.getBody().getStatus());
        verify(productService, times(1)).getParticularInventory(1L);

        logger.info("getParticularInventory test passed.");
    }

    @Test
    void testAddInventory() {
        // Test case for adding a new product to the inventory
        logger.info("Testing addInventory method...");

        Product productModel = new Product();
        productModel.setName("Test Product");
        productModel.setDescription("Test Description");
        productModel.setPrice(100.0);
        productModel.setQuantity(10);

        com.inventory.inventory_management.entities.Product product = com.inventory.inventory_management.entities.Product.builder()
                .name(productModel.getName())
                .description(productModel.getDescription())
                .price(productModel.getPrice())
                .quantity(productModel.getQuantity())
                .build();

        when(productService.addInventory(any(Product.class))).thenReturn(product);

        // Calling the controller method
        ResponseEntity<Response> response = inventoryController.addInventory(productModel);

        // Asserting the response status and data
        assertEquals("success", response.getBody().getStatus());
        verify(productService, times(1)).addInventory(any(Product.class));

        logger.info("addInventory test passed.");
    }

    @Test
    void testDeleteInventory() {
        // Test case for deleting a product from the inventory
        logger.info("Testing deleteInventory method...");

        com.inventory.inventory_management.entities.Product product = new com.inventory.inventory_management.entities.Product();
        when(productService.getParticularInventory(1L)).thenReturn(product);
        doNothing().when(productService).deleteInventory(1L);

        // Calling the controller method
        ResponseEntity<Response> response = inventoryController.deleteInventory(1L);

        // Asserting the response status and data
        assertEquals("success", response.getBody().getStatus());
        verify(productService, times(1)).getParticularInventory(1L);
        verify(productService, times(1)).deleteInventory(1L);

        logger.info("deleteInventory test passed.");
    }

    @Test
    void updateInventorySuccessfully() {
        // Test case for successfully updating inventory details
        logger.info("Testing updateInventory successfully...");

        Product product = new Product();
        product.setPrice(200.0);
        product.setQuantity(20);
        product.setUser("testUser");

        Response response = new Response(Constants.SUCCESS, "Inventory item updated successfully", product);
        when(productService.updateInventory(1L, 200.0, 20, "testUser")).thenReturn(response);

        // Calling the controller method
        ResponseEntity<Response> result = inventoryController.updateInventory(1L, 200.0, 20, "testUser");

        // Asserting the response status and data
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().getStatus());
        verify(productService, times(1)).updateInventory(1L, 200.0, 20, "testUser");

        logger.info("updateInventory successfully test passed.");
    }

    @Test
    void updateInventoryInvalidProductId() {
        // Test case for invalid product ID when updating inventory
        logger.info("Testing updateInventory with invalid product ID...");

        when(productService.updateInventory(1L, 200.0, 20, "testUser")).thenReturn(null);

        // Calling the controller method
        ResponseEntity<Response> result = inventoryController.updateInventory(1L, 200.0, 20, "testUser");

        // Asserting the response status and data
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error", result.getBody().getStatus());
        assertEquals("Invalid product ID", result.getBody().getData());
        verify(productService, times(1)).updateInventory(1L, 200.0, 20, "testUser");

        logger.info("updateInventory with invalid product ID test passed.");
    }

    @Test
    void updateInventoryInternalServerError() {
        // Test case for internal server error when updating inventory
        logger.info("Testing updateInventory with internal server error...");

        when(productService.updateInventory(1L, 200.0, 20, "testUser")).thenThrow(new RuntimeException("Database error"));

        // Calling the controller method
        ResponseEntity<Response> result = inventoryController.updateInventory(1L, 200.0, 20, "testUser");

        // Asserting the response status and data
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error", result.getBody().getStatus());
        assertEquals("Internal Server Error", result.getBody().getMessage());
        verify(productService, times(1)).updateInventory(1L, 200.0, 20, "testUser");

        logger.info("updateInventory with internal server error test passed.");
    }

    @Test
    void testGetInventoryError() {
        // Test case for handling errors when fetching inventory
        logger.info("Testing getInventory method with error...");

        when(productService.getInventory()).thenThrow(new RuntimeException("Database error"));

        // Calling the controller method
        ResponseEntity<Response> response = inventoryController.getInventory();

        // Asserting the response status and data
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error", response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getMessage());
        assertEquals("An error occurred while fetching inventory: Database error", response.getBody().getData());
        verify(productService, times(1)).getInventory();

        logger.info("getInventory with error test passed.");
    }

    @Test
    void testDeleteInventoryError() {
        // Test case for handling errors during product deletion
        logger.info("Testing deleteInventory method with error...");

        Long inventoryId = 1L;
        when(productService.getParticularInventory(inventoryId)).thenReturn(new com.inventory.inventory_management.entities.Product());
        doThrow(new RuntimeException("Database error")).when(productService).deleteInventory(inventoryId);

        // Calling the controller method
        ResponseEntity<Response> response = inventoryController.deleteInventory(inventoryId);

        // Asserting the response status and data
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error", response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getMessage());
        assertEquals("An error occurred while deleting inventory item: Database error", response.getBody().getData());
        verify(productService, times(1)).getParticularInventory(inventoryId);
        verify(productService, times(1)).deleteInventory(inventoryId);

        logger.info("deleteInventory with error test passed.");
    }

    // Additional tests would follow the same structure, applying proper logging and assertions
}
