package com.inventory.inventory_management;

import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.model.User;
import com.inventory.inventory_management.repository.ProductRepository;
import com.inventory.inventory_management.repository.UserRepository;
import com.inventory.inventory_management.service.ProductService;
import com.inventory.inventory_management.util.StockMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);  // Logger initialization

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        logger.info("Test setup complete.");
    }

    // Test for retrieving the full inventory
    @Test
    void getInventory_ReturnsProductList() {
        logger.info("Running test: getInventory_ReturnsProductList");

        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));  // Mock repository

        List<Product> products = productService.getInventory();

        assertEquals(2, products.size());  // Verify that the number of products is correct
        logger.info("Test passed: Retrieved inventory with {} products.", products.size());
    }

    // Test for exception when retrieving inventory
    @Test
    void getInventory_ThrowsException() {
        logger.info("Running test: getInventory_ThrowsException");

        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));  // Simulate exception

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getInventory();
        });

        assertEquals("Database error", exception.getMessage());  // Validate exception message
        logger.error("Test failed: Error occurred while retrieving inventory - {}", exception.getMessage());
    }

    // Test for retrieving a particular product
    @Test
    void getParticularInventory_ProductExists_ReturnsProduct() {
        logger.info("Running test: getParticularInventory_ProductExists_ReturnsProduct");

        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));  // Mock product retrieval

        Product foundProduct = productService.getParticularInventory(1L);

        assertNotNull(foundProduct);
        assertEquals(1L, foundProduct.getId());  // Verify product ID
        logger.info("Test passed: Retrieved product with ID: {}", foundProduct.getId());
    }

    @Test
    void getParticularInventory_ProductDoesNotExist_ThrowsException() {
        logger.info("Running test: getParticularInventory_ProductDoesNotExist_ThrowsException");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());  // Mock no product found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getParticularInventory(1L);
        });

        assertEquals("Product not found", exception.getMessage());  // Validate exception message
        logger.error("Test failed: Product not found.");
    }

    // Test for adding a valid product to the inventory
    @Test
    void addInventory_ValidProduct_ReturnsSavedProduct() {
        logger.info("Running test: addInventory_ValidProduct_ReturnsSavedProduct");

        com.inventory.inventory_management.model.Product modelProduct = new com.inventory.inventory_management.model.Product();
        modelProduct.setName("Test Product");
        modelProduct.setDescription("Test Description");
        modelProduct.setPrice(100.0);
        modelProduct.setQuantity(10);
        modelProduct.setUser("Admin");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setQuantity(10);
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        product.setCreatedBy("Admin");

        when(userRepository.findByUsername("Admin")).thenReturn(Optional.of(new User()));
        when(productRepository.findByNameAndDescription("Test Product", "Test Description")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);  // Mock product save

        Product savedProduct = productService.addInventory(modelProduct);

        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());  // Validate name
        assertEquals("Admin", savedProduct.getCreatedBy());  // Validate creator
        logger.info("Test passed: Product added successfully with ID: {}", savedProduct.getId());
    }

    // Test for adding a product with an existing name and description
    @Test
    void addInventory_ThrowsException_DuplicateProduct() {
        logger.info("Running test: addInventory_ThrowsException_DuplicateProduct");

        com.inventory.inventory_management.model.Product modelProduct = new com.inventory.inventory_management.model.Product();
        modelProduct.setName("Test Product");
        modelProduct.setDescription("Test Description");
        modelProduct.setPrice(100.0);
        modelProduct.setQuantity(10);
        modelProduct.setUser("Admin");

        when(userRepository.findByUsername("Admin")).thenReturn(Optional.of(new User()));
        when(productRepository.findByNameAndDescription("Test Product", "Test Description")).thenReturn(Optional.of(new Product()));  // Mock duplicate check

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addInventory(modelProduct);
        });

        assertEquals("A product with the same name and description already exists", exception.getMessage());  // Validate exception message
        logger.error("Test failed: Duplicate product found.");
    }

    // Test for deleting a product successfully
    @Test
    void deleteInventory_ProductExists_DeletesProduct() {
        logger.info("Running test: deleteInventory_ProductExists_DeletesProduct");

        doNothing().when(productRepository).deleteById(1L);  // Mock delete

        productService.deleteInventory(1L);

        verify(productRepository).deleteById(1L);  // Verify that delete was called
        logger.info("Test passed: Product with ID 1 deleted successfully.");
    }

    // Test for adding stock to an existing product
    @Test
    void addStock_ProductExists_AddsStock() {
        logger.info("Running test: addStock_ProductExists_AddsStock");

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product updatedProduct = invocation.getArgument(0);
            updatedProduct.setQuantity(15);  // Simulate stock addition
            return updatedProduct;
        });

        StockMovement stockResponse = productService.addStock(1L, 5);

        assertNotNull(stockResponse);
        assertEquals("15", stockResponse.getStockLevel());  // Verify new stock level
        verify(productRepository).save(any(Product.class));  // Ensure save was called
        logger.info("Test passed: Stock added successfully, new stock level: {}", stockResponse.getStockLevel());
    }

    // Test for adding stock when product doesn't exist
    @Test
    void addStock_ProductDoesNotExist_ThrowsException() {
        logger.info("Running test: addStock_ProductDoesNotExist_ThrowsException");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());  // Mock product not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.addStock(1L, 5));
        assertEquals("Product not found", exception.getMessage());  // Validate exception message
        logger.error("Test failed: Product not found.");
    }

    // Test for removing stock from an existing product
    @Test
    void removeStock_ProductExists_RemovesStock() {
        logger.info("Running test: removeStock_ProductExists_RemovesStock");

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product updatedProduct = invocation.getArgument(0);
            updatedProduct.setQuantity(5);  // Simulate stock removal
            return updatedProduct;
        });

        StockMovement stockResponse = productService.removeStock(1L, 5);

        assertNotNull(stockResponse);
        assertEquals("5", stockResponse.getStockLevel());  // Verify new stock level
        verify(productRepository).save(any(Product.class));  // Ensure save was called
        logger.info("Test passed: Stock removed successfully, new stock level: {}", stockResponse.getStockLevel());
    }

    // Test for removing stock when product doesn't exist
    @Test
    void removeStock_ProductDoesNotExist_ThrowsException() {
        logger.info("Running test: removeStock_ProductDoesNotExist_ThrowsException");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());  // Mock product not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.removeStock(1L, 5));
        assertEquals("Product not found", exception.getMessage());  // Validate exception message
        logger.error("Test failed: Product not found.");
    }

    // Test for getting the stock level of a product
    @Test
    void getStockLevel_ProductExists_ReturnsStockLevel() {
        logger.info("Running test: getStockLevel_ProductExists_ReturnsStockLevel");

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        StockMovement stockResponse = productService.getStockLevel(1L);

        assertNotNull(stockResponse);
        assertEquals("10", stockResponse.getStockLevel());  // Ensure correct stock level
        logger.info("Test passed: Retrieved stock level for product ID 1: {}", stockResponse.getStockLevel());
    }

    // Test for getting stock level when product doesn't exist
    @Test
    void getStockLevel_ProductDoesNotExist_ThrowsException() {
        logger.info("Running test: getStockLevel_ProductDoesNotExist_ThrowsException");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());  // Mock product not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getStockLevel(1L));
        assertEquals("Product not found", exception.getMessage());  // Validate exception message
        logger.error("Test failed: Product not found.");
    }



}
