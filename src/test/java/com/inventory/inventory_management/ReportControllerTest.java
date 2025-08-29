package com.inventory.inventory_management;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.inventory.inventory_management.controller.ReportController;
import com.inventory.inventory_management.entities.Order;
import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportControllerTest {

    // Logger for logging events and errors
    private static final Logger logger = LoggerFactory.getLogger(ReportControllerTest.class);

    @InjectMocks
    private ReportController reportController;  // Controller under test

    @Mock
    private ReportService reportService;  // Mocked service used by the controller

    @Mock
    private HttpServletResponse response;  // Mocked HttpServletResponse for testing HTTP responses

    @BeforeEach
    public void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
        logger.info("Test setup completed");
    }

    // Test for handling internal server error while fetching the inventory report
    @Test
    public void getInventoryReport_InternalServerError() {
        // Simulate exception thrown by reportService
        when(reportService.getInventoryReport()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Response> responseEntity = reportController.getInventoryReport();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("An error occurred while fetching inventory report: Database error", responseEntity.getBody().getData());

        logger.error("Error occurred while fetching inventory report: Database error");
    }

    // Test for handling internal server error while fetching the order report
    @Test
    public void getOrderReport_InternalServerError() {
        // Simulate exception thrown by reportService
        when(reportService.getOrderReport()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Response> responseEntity = reportController.getOrderReport();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("An error occurred while fetching order report: Database error", responseEntity.getBody().getData());

        logger.error("Error occurred while fetching order report: Database error");
    }

    // Test for handling error when exporting inventory report to CSV
    @Test
    public void exportInventoryReportToCsv_InternalServerError() throws IOException {
        // Simulate exception thrown by reportService during file export
        when(reportService.exportInventoryReportToExcel()).thenThrow(new IOException("File system error"));

        ResponseEntity<Response> responseEntity = reportController.exportInventoryReportToCsv();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("An error occurred while exporting inventory report: File system error", responseEntity.getBody().getData());

        logger.error("Error occurred while exporting inventory report: File system error");
    }

    // Test for handling error when exporting order report to CSV
    @Test
    public void exportOrderReportToCsv_InternalServerError() throws IOException {
        // Simulate exception thrown by reportService during file export
        when(reportService.exportOrderReportToExcel()).thenThrow(new IOException("File system error"));

        ResponseEntity<Response> responseEntity = reportController.exportOrderReportToCsv();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody().getStatus());
        assertEquals("An error occurred while exporting order report: File system error", responseEntity.getBody().getData());

        logger.error("Error occurred while exporting order report: File system error");
    }

    // Test for successfully fetching inventory report
    @Test
    public void getInventoryReport_Success() {
        List<Product> products = List.of(new Product(), new Product());  // Create mock product list
        when(reportService.getInventoryReport()).thenReturn(products);  // Simulate successful report fetch

        ResponseEntity<Response> responseEntity = reportController.getInventoryReport();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("success", responseEntity.getBody().getStatus());
        assertEquals("Inventory report fetched successfully", responseEntity.getBody().getMessage());
        assertEquals(products, responseEntity.getBody().getData());

        logger.info("Inventory report fetched successfully");
    }

    // Test for successfully fetching order report
    @Test
    public void getOrderReport_Success() {
        List<Order> orders = List.of(new Order(), new Order());  // Create mock order list
        when(reportService.getOrderReport()).thenReturn(orders);  // Simulate successful report fetch

        ResponseEntity<Response> responseEntity = reportController.getOrderReport();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("success", responseEntity.getBody().getStatus());
        assertEquals("Order report fetched successfully", responseEntity.getBody().getMessage());
        assertEquals(orders, responseEntity.getBody().getData());

        logger.info("Order report fetched successfully");
    }

    // Test for successfully exporting inventory report to CSV
    @Test
    public void exportInventoryReportToCsv_Success() throws IOException {
        String filePath = "/path/to/inventory_report.csv";  // Simulate file path for the exported report
        when(reportService.exportInventoryReportToExcel()).thenReturn(filePath);  // Simulate successful export

        ResponseEntity<Response> responseEntity = reportController.exportInventoryReportToCsv();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("success", responseEntity.getBody().getStatus());
        assertEquals("Inventory report exported successfully", responseEntity.getBody().getMessage());
        assertEquals("Inventory Report saved at: " + filePath, responseEntity.getBody().getData());

        logger.info("Inventory report exported successfully to: " + filePath);
    }

    // Test for successfully exporting order report to CSV
    @Test
    public void exportOrderReportToCsv_Success() throws IOException {
        String filePath = "/path/to/order_report.csv";  // Simulate file path for the exported report
        when(reportService.exportOrderReportToExcel()).thenReturn(filePath);  // Simulate successful export

        ResponseEntity<Response> responseEntity = reportController.exportOrderReportToCsv();

        // Assert expected HTTP response code and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("success", responseEntity.getBody().getStatus());
        assertEquals("Order report exported successfully", responseEntity.getBody().getMessage());
        assertEquals("Order Report saved at: " + filePath, responseEntity.getBody().getData());

        logger.info("Order report exported successfully to: " + filePath);
    }
}
