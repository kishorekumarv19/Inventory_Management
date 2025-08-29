package com.inventory.inventory_management;

import com.inventory.inventory_management.entities.Order;
import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.repository.OrderRepository;
import com.inventory.inventory_management.repository.ProductRepository;
import com.inventory.inventory_management.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceTest.class);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReportService reportService;

    private String generatedFilePath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger.info("Mocks initialized for ReportServiceTest");
    }

    @AfterEach
    void tearDown() {
        if (generatedFilePath != null) {
            File file = new File(generatedFilePath);
            if (file.exists()) {
                if (file.delete()) {
                    logger.info("Temporary test file deleted: {}", generatedFilePath);
                } else {
                    logger.warn("Failed to delete temporary test file: {}", generatedFilePath);
                }
            }
        }
    }

    // Test exporting inventory report to Excel file with product data
    @Test
    void exportInventoryReportToExcel_CreatesExcelFile() throws IOException {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setQuantity(10);

        when(productRepository.findAll()).thenReturn(List.of(product));

        logger.info("Testing inventory export with a single product...");
        String filePath = reportService.exportInventoryReportToExcel();
        generatedFilePath = filePath;

        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        logger.info("Inventory Excel file created: {}", filePath);
    }

    // Test exporting order report with one order
    @Test
    void exportOrderReportToExcel_CreatesExcelFile() throws IOException {
        Order order = new Order();
        order.setId(1L);
        order.setQuantity(5);
        order.setTotalPrice(500.0);
        order.setStatus("NEW");
        order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        when(orderRepository.findAll()).thenReturn(List.of(order));

        logger.info("Testing order export with a single order...");
        String filePath = reportService.exportOrderReportToExcel();
        generatedFilePath = filePath;

        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        logger.info("Order Excel file created: {}", filePath);
    }

    // Test for multiple products in inventory report export
    @Test
    void exportInventoryReportToExcel_MultipleProducts_CreatesExcelFileWithMultipleEntries() throws IOException {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(100.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(200.0);
        product2.setQuantity(20);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        logger.info("Testing inventory export with multiple products...");
        String filePath = reportService.exportInventoryReportToExcel();
        generatedFilePath = filePath;

        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        logger.info("Inventory Excel file with multiple products created: {}", filePath);
    }

    // Test for multiple orders in order report export
    @Test
    void exportOrderReportToExcel_MultipleOrders_CreatesExcelFileWithMultipleEntries() throws IOException {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setQuantity(5);
        order1.setTotalPrice(500.0);
        order1.setStatus("NEW");
        order1.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setQuantity(10);
        order2.setTotalPrice(1000.0);
        order2.setStatus("SHIPPED");
        order2.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        logger.info("Testing order export with multiple orders...");
        String filePath = reportService.exportOrderReportToExcel();
        generatedFilePath = filePath;

        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        logger.info("Order Excel file with multiple orders created: {}", filePath);
    }

    // Test exporting order report when no orders are present
    @Test
    void exportOrderReportToExcel_NoOrders_ReturnsEmptyFile() throws IOException {
        when(orderRepository.findAll()).thenReturn(List.of());

        logger.info("Testing order export with no data...");
        String filePath = reportService.exportOrderReportToExcel();
        generatedFilePath = filePath;

        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        logger.info("Empty order Excel file generated successfully: {}", filePath);
    }

    // Test exporting inventory report when no products are present
    @Test
    void exportInventoryReportToExcel_NoProducts_ReturnsEmptyFile() throws IOException {
        when(productRepository.findAll()).thenReturn(List.of());

        logger.info("Testing inventory export with no data...");
        String filePath = reportService.exportInventoryReportToExcel();
        generatedFilePath = filePath;

        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        logger.info("Empty inventory Excel file generated successfully: {}", filePath);
    }

    // Test getInventoryReport returns list of products
    @Test
    void getInventoryReport_ProductsPresent_ReturnsProductList() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setQuantity(10);

        when(productRepository.findAll()).thenReturn(List.of(product));

        logger.info("Testing fetching inventory report with one product...");
        List<Product> products = reportService.getInventoryReport();

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getName());
    }

    // Test getOrderReport returns list of orders
    @Test
    void getOrderReport_OrdersPresent_ReturnsOrderList() {
        Order order = new Order();
        order.setId(1L);
        order.setQuantity(5);
        order.setTotalPrice(500.0);
        order.setStatus("NEW");
        order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        when(orderRepository.findAll()).thenReturn(List.of(order));

        logger.info("Testing fetching order report with one order...");
        List<Order> orders = reportService.getOrderReport();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("NEW", orders.get(0).getStatus());
    }

    // Test exception is thrown if productRepository throws error
    @Test
    void getInventoryReport_RepositoryThrowsException_ThrowsException() {
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        logger.info("Testing inventory report exception handling...");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reportService.getInventoryReport();
        });

        assertEquals("Database error", exception.getMessage());
        logger.error("Expected exception caught: {}", exception.getMessage());
    }

    // Test exception is thrown if orderRepository throws error
    @Test
    void getOrderReport_RepositoryThrowsException_ThrowsException() {
        when(orderRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        logger.info("Testing order report exception handling...");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reportService.getOrderReport();
        });

        assertEquals("Database error", exception.getMessage());
        logger.error("Expected exception caught: {}", exception.getMessage());
    }
}
