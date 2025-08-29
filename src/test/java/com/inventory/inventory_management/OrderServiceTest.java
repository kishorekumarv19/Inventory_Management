package com.inventory.inventory_management;

import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.model.Order;
import com.inventory.inventory_management.model.User;
import com.inventory.inventory_management.repository.OrderRepository;
import com.inventory.inventory_management.repository.ProductRepository;
import com.inventory.inventory_management.repository.UserRepository;
import com.inventory.inventory_management.service.OrderService;
import com.inventory.inventory_management.model.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceTest.class);

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private User user;

    @Mock
    private Product product;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setUser("testUser");
        order.setProductId(1L);
        order.setQuantity(2);
    }

    // Test case 1: Create Order - Successful
    @Test
    void testCreateOrder_Success() {
        logger.info("Running testCreateOrder_Success");

        // Mock user existence
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Mock product existence and stock
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(product.getQuantity()).thenReturn(5);
        when(product.getPrice()).thenReturn(100.0);

        // Mock orderRepository save (this is the actual save of the order)
        when(orderRepository.save(any())).thenReturn(new com.inventory.inventory_management.entities.Order());

        // Call the method
        com.inventory.inventory_management.entities.Order createdOrder = orderService.createOrder(order);

        // Assertions
        assertNotNull(createdOrder);

        // Verify that only the orderRepository.save is called, not productRepository.save
        verify(orderRepository, times(1)).save(any());

        // Optionally, you can check that findById was called to retrieve the product
        verify(productRepository, times(1)).findById(1L);

        logger.info("testCreateOrder_Success passed");
    }

    // Test case 2: Create Order - User Not Found
    @Test
    void testCreateOrder_UserNotFound() {
        logger.info("Running testCreateOrder_UserNotFound");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("User not found", exception.getMessage());
        verify(orderRepository, times(0)).save(any());

        logger.info("testCreateOrder_UserNotFound passed");
    }

    // Test case 3: Create Order - Insufficient Stock
    @Test
    void testCreateOrder_InsufficientStock() {
        logger.info("Running testCreateOrder_InsufficientStock");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(product.getQuantity()).thenReturn(1); // Insufficient stock
        when(product.getPrice()).thenReturn(100.0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Insufficient stock for the product", exception.getMessage());
        verify(orderRepository, times(0)).save(any());

        logger.info("testCreateOrder_InsufficientStock passed");
    }

    // Test case 4: Get All Orders - Success
    @Test
    void testGetAllOrders_Success() {
        logger.info("Running testGetAllOrders_Success");

        List<com.inventory.inventory_management.entities.Order> orders = new ArrayList<>();
        orders.add(new com.inventory.inventory_management.entities.Order());

        when(orderRepository.findAll()).thenReturn(orders);

        List<com.inventory.inventory_management.entities.Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        logger.info("testGetAllOrders_Success passed");
    }

    // Test case 5: Get Order by ID - Order Found
    @Test
    void testGetOrderById_Success() {
        logger.info("Running testGetOrderById_Success");

        com.inventory.inventory_management.entities.Order existingOrder = new com.inventory.inventory_management.entities.Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        com.inventory.inventory_management.entities.Order result = orderService.getOrderById(1L);

        assertNotNull(result);

        logger.info("testGetOrderById_Success passed");
    }

    // Test case 6: Get Order by ID - Order Not Found
    @Test
    void testGetOrderById_OrderNotFound() {
        logger.info("Running testGetOrderById_OrderNotFound");

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertEquals("Order not found", exception.getMessage());

        logger.info("testGetOrderById_OrderNotFound passed");
    }

    // Test case 9: Cancel Order - Success
    @Test
    void testCancelOrder_Success() {
        logger.info("Running testCancelOrder_Success");

        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        ResponseEntity<Response> response = orderService.cancelOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order successfully deleted", response.getBody().getMessage());

        logger.info("testCancelOrder_Success passed");
    }

    // Test case 10: Cancel Order - Order Not Found
    @Test
    void testCancelOrder_OrderNotFound() {
        logger.info("Running testCancelOrder_OrderNotFound");

        when(orderRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Response> response = orderService.cancelOrder(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody().getMessage());

        logger.info("testCancelOrder_OrderNotFound passed");
    }

    // Test case: Update Order Status - Valid Status
    @Test
    void testUpdateOrderStatus_ValidStatus_Success() {
        logger.info("Running testUpdateOrderStatus_ValidStatus_Success");

        try {
            Product product = new Product();
            product.setId(1L);
            product.setQuantity(10);
            product.setPrice(100.0);

            com.inventory.inventory_management.entities.Order order = new com.inventory.inventory_management.entities.Order();
            order.setId(1L);
            order.setStatus("PROCESSING");
            order.setQuantity(2);
            order.setTotalPrice(200.0);

            order.setProductId(product);

            User user = new User();
            user.setUsername("testUser");

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(orderRepository.save(any())).thenReturn(order); // ← Add this if missing

            com.inventory.inventory_management.entities.Order updatedOrder =
                    orderService.updateOrderStatus(1L, "SHIPPED", "testUser");

            assertNotNull(updatedOrder);
            assertEquals("SHIPPED", updatedOrder.getStatus());

            logger.info("testUpdateOrderStatus_ValidStatus_Success passed");
        } catch (Exception e) {
            logger.error("Exception occurred during testUpdateOrderStatus_ValidStatus_Success: " + e.getMessage(), e);
            fail("Exception occurred: " + e.getMessage());
        }
    }

    // Test case: Update Order Status - Invalid Status
    @Test
    void testUpdateOrderStatus_InvalidStatus_ThrowsException() {
        logger.info("Running testUpdateOrderStatus_InvalidStatus_ThrowsException");

        com.inventory.inventory_management.entities.Order order = new com.inventory.inventory_management.entities.Order();
        order.setId(1L);
        order.setStatus("PROCESSING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, "INVALID_STATUS", "testUser");
        });

        assertEquals("User not found", exception.getMessage());
        verify(orderRepository, times(0)).save(any());

        logger.info("testUpdateOrderStatus_InvalidStatus_ThrowsException passed");
    }

    // Test case: Update Order Status - Order Not Found
    @Test
    void testUpdateOrderStatus_OrderNotFound_ThrowsException() {
        logger.info("Running testUpdateOrderStatus_OrderNotFound_ThrowsException");

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, "SHIPPED", "testUser");
        });

        assertEquals("Order not found", exception.getMessage());

        logger.info("testUpdateOrderStatus_OrderNotFound_ThrowsException passed");
    }

    // Test case: Update Order Status - User Not Found
    @Test
    void testUpdateOrderStatus_UserNotFound_ThrowsException() {
        logger.info("Running testUpdateOrderStatus_UserNotFound_ThrowsException");

        com.inventory.inventory_management.entities.Order order = new com.inventory.inventory_management.entities.Order();
        order.setId(1L);
        order.setStatus("PROCESSING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, "SHIPPED", "testUser");
        });

        assertEquals("User not found", exception.getMessage());

        logger.info("testUpdateOrderStatus_UserNotFound_ThrowsException passed");
    }
}
