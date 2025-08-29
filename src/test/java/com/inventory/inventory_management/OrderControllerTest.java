package com.inventory.inventory_management;

import com.inventory.inventory_management.controller.OrderController;
import com.inventory.inventory_management.entities.Order;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.OrderService;
import com.inventory.inventory_management.util.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    // Logger for this test class
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(OrderControllerTest.class);

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    private Order order;

    // Setup method to initialize test data before each test
    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data...");
        order = new Order();
        order.setId(1L);
        order.setQuantity(10);
        order.setStatus("Pending");
        logger.info("Test data initialized: Order ID = 1, Quantity = 10, Status = Pending");
    }

    // Test case for successfully creating an order
    @Test
    public void testCreateOrder_Success() {
        logger.info("Running test: Create Order - Success");

        // Mocking OrderService
        when(orderService.createOrder(any())).thenReturn(order);

        ResponseEntity<Response> response = orderController.createOrder(new com.inventory.inventory_management.model.Order());

        // Assert success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.SUCCESS, response.getBody().getStatus());
        assertEquals("Order created successfully", response.getBody().getMessage());

        logger.info("Test passed: Order successfully created.");
    }

    // Test case for failing order creation
    @Test
    public void testCreateOrder_Failure() {
        logger.info("Running test: Create Order - Failure");

        // Mocking OrderService to return null for failure
        when(orderService.createOrder(any())).thenReturn(null);

        ResponseEntity<Response> response = orderController.createOrder(new com.inventory.inventory_management.model.Order());

        // Assert failure response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertEquals("Order creation failed", response.getBody().getMessage());

        logger.info("Test passed: Order creation failed.");
    }

    // Test case for order creation throwing an exception
    @Test
    public void testCreateOrder_Exception() {
        logger.info("Running test: Create Order - Exception");

        // Mocking OrderService to throw exception
        when(orderService.createOrder(any())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Response> response = orderController.createOrder(new com.inventory.inventory_management.model.Order());

        // Assert exception response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Order creation failed"));

        logger.error("Test passed: Order creation failed with exception.");
    }

    // Test case for fetching an order by ID successfully
    @Test
    public void testGetOrderById_Success() {
        logger.info("Running test: Get Order By ID - Success");

        // Mocking OrderService
        when(orderService.getOrderById(1L)).thenReturn(order);

        ResponseEntity<Response> response = orderController.getOrderById(1L);

        // Assert success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.SUCCESS, response.getBody().getStatus());
        assertEquals("Order fetched successfully", response.getBody().getMessage());

        logger.info("Test passed: Order fetched successfully.");
    }

    // Test case for fetching an order by ID when not found
    @Test
    public void testGetOrderById_NotFound() {
        logger.info("Running test: Get Order By ID - Not Found");

        // Mocking OrderService to return null when order is not found
        when(orderService.getOrderById(1L)).thenReturn(null);

        ResponseEntity<Response> response = orderController.getOrderById(1L);

        // Assert not found response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertEquals("Order not found", response.getBody().getMessage());

        logger.info("Test passed: Order not found.");
    }

    // Test case for fetching an order by ID when an exception occurs
    @Test
    void testGetOrderById_Exception() {
        logger.info("Running test: Get Order By ID - Exception");

        // Mocking OrderService to throw an exception
        when(orderService.getOrderById(1L)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = orderController.getOrderById(1L);

        // Assert server error response
        assertTrue(response.getStatusCode().is5xxServerError());

        logger.error("Test passed: Exception occurred while fetching order.");
    }

    // Test case for successfully fetching all orders
    @Test
    public void testGetAllOrders_Success() {
        logger.info("Running test: Get All Orders - Success");

        // Mocking OrderService
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        ResponseEntity<Response> response = orderController.getAllOrders();

        // Assert success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.SUCCESS, response.getBody().getStatus());
        assertEquals("Orders fetched successfully", response.getBody().getMessage());

        logger.info("Test passed: All orders fetched successfully.");
    }

    // Test case for fetching all orders when the list is empty
    @Test
    public void testGetAllOrders_EmptyList() {
        logger.info("Running test: Get All Orders - Empty List");

        // Mocking OrderService to return an empty list
        when(orderService.getAllOrders()).thenReturn(List.of());

        ResponseEntity<Response> response = orderController.getAllOrders();

        // Assert empty list response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertEquals("No data in database", response.getBody().getMessage());

        logger.info("Test passed: No orders found.");
    }

    // Test case for fetching all orders when an exception occurs
    @Test
    public void testGetAllOrders_Exception() {
        logger.info("Running test: Get All Orders - Exception");

        // Mocking OrderService to throw an exception
        when(orderService.getAllOrders()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<Response> response = orderController.getAllOrders();

        // Assert error response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getMessage());
        assertTrue(((String) response.getBody().getData()).contains("An error occurred while fetching orders"));

        logger.error("Test passed: Exception occurred while fetching orders.");
    }

    // Test case for successfully updating an order's status
    @Test
    public void testUpdateOrderStatus_Success() {
        logger.info("Running test: Update Order Status - Success");

        // Mocking OrderService
        when(orderService.updateOrderStatus(1L, "Shipped", "admin")).thenReturn(order);

        ResponseEntity<Response> response = orderController.updateOrderStatus(1L, "Shipped", "admin");

        // Assert success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.SUCCESS, response.getBody().getStatus());
        assertEquals("Order status updated successfully", response.getBody().getMessage());

        logger.info("Test passed: Order status updated successfully.");
    }

    // Test case for failing to update an order's status
    @Test
    public void testUpdateOrderStatus_Failure() {
        logger.info("Running test: Update Order Status - Failure");

        // Mocking OrderService to return null for failure
        when(orderService.updateOrderStatus(1L, "Shipped", "admin")).thenReturn(null);

        ResponseEntity<Response> response = orderController.updateOrderStatus(1L, "Shipped", "admin");

        // Assert failure response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertEquals("Order update failed", response.getBody().getMessage());

        logger.info("Test passed: Order status update failed.");
    }

    // Test case for successfully cancelling an order
    @Test
    public void testCancelOrder_Success() {
        logger.info("Running test: Cancel Order - Success");

        Response expectedResponse = new Response(Constants.SUCCESS, "Order cancelled successfully", null);
        when(orderService.cancelOrder(1L)).thenReturn(ResponseEntity.ok(expectedResponse));

        ResponseEntity<Response> response = orderController.cancelOrder(1L);

        // Assert success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.SUCCESS, response.getBody().getStatus());
        assertEquals("Order cancelled successfully", response.getBody().getMessage());

        logger.info("Test passed: Order cancelled successfully.");
    }

    // Test case for an exception while cancelling an order
    @Test
    public void testCancelOrder_Exception() {
        logger.info("Running test: Cancel Order - Exception");

        // Mocking OrderService to throw an exception
        when(orderService.cancelOrder(1L)).thenThrow(new RuntimeException("Order cancellation failed"));

        ResponseEntity<Response> response = orderController.cancelOrder(1L);

        // Assert error response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Constants.ERROR, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("An error occurred while canceling the order"));

        logger.error("Test passed: Exception occurred while cancelling order.");
    }
}
