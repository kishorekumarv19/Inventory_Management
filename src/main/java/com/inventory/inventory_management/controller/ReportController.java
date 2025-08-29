package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.entities.Order;
import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.service.ReportService;
import com.inventory.inventory_management.util.Constants;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    // Injecting ReportService via @Autowired, which is responsible for handling the business logic
    @Autowired
    private ReportService reportService;

    // Logger to log info, error, and debug messages for the application
    private static final Logger logger = LogManager.getLogger(ReportController.class);

    /**
     * Retrieves the inventory report.
     *
     * This method fetches a list of products from the ReportService and returns them as part of a response entity.
     * In case of an error, a detailed error message is logged and returned.
     *
     * @return a response entity containing the inventory report or an error message.
     */
    @GetMapping("/inventory")
    public ResponseEntity<Response> getInventoryReport() {
        try {
            logger.info("Fetching inventory report"); // Log the info message before fetching the report
            List<Product> products = reportService.getInventoryReport(); // Call the service method to get the inventory report
            logger.info("Inventory report fetched successfully"); // Log successful fetching of the report
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Inventory report fetched successfully", products));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching inventory report", ex); // Log the error with the exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while fetching inventory report: " + ex.getMessage()));
        }
    }

    /**
     * Retrieves the order report.
     *
     * This method fetches a list of orders from the ReportService and returns them as part of a response entity.
     * In case of an error, a detailed error message is logged and returned.
     *
     * @return a response entity containing the order report or an error message.
     */
    @GetMapping("/orders")
    public ResponseEntity<Response> getOrderReport() {
        try {
            logger.info("Fetching order report"); // Log the info message before fetching the order report
            List<Order> orders = reportService.getOrderReport(); // Call the service method to get the order report
            logger.info("Order report fetched successfully"); // Log successful fetching of the report
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Order report fetched successfully", orders));
        } catch (Exception ex) {
            logger.error("An error occurred while fetching order report", ex); // Log the error with the exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while fetching order report: " + ex.getMessage()));
        }
    }

    /**
     * Exports the inventory report to a CSV file.
     *
     * This method calls the service to export the inventory report to an Excel file and returns the file path in the response.
     * In case of an error, a detailed error message is logged and returned.
     *
     * @return a response entity containing the file path of the exported inventory report or an error message.
     */
    @GetMapping("/inventory/csv")
    public ResponseEntity<Response> exportInventoryReportToCsv() {
        try {
            logger.info("Exporting inventory report to CSV"); // Log the info message before starting the export process
            String filePath = reportService.exportInventoryReportToExcel(); // Call the service method to export the report
            logger.info("Inventory report exported successfully"); // Log successful export of the report
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Inventory report exported successfully", "Inventory Report saved at: " + filePath));
        } catch (IOException ex) {
            logger.error("An error occurred while exporting inventory report to CSV", ex); // Log the error with the exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while exporting inventory report: " + ex.getMessage()));
        }
    }

    /**
     * Exports the order report to a CSV file.
     *
     * This method calls the service to export the order report to an Excel file and returns the file path in the response.
     * In case of an error, a detailed error message is logged and returned.
     *
     * @return a response entity containing the file path of the exported order report or an error message.
     */
    @GetMapping("/orders/csv")
    public ResponseEntity<Response> exportOrderReportToCsv() {
        try {
            logger.info("Exporting order report to CSV"); // Log the info message before starting the export process
            String filePath = reportService.exportOrderReportToExcel(); // Call the service method to export the report
            logger.info("Order report exported successfully"); // Log successful export of the report
            return ResponseEntity.ok(new Response(Constants.SUCCESS, "Order report exported successfully", "Order Report saved at: " + filePath));
        } catch (IOException ex) {
            logger.error("An error occurred while exporting order report to CSV", ex); // Log the error with the exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(Constants.ERROR, "Internal Server Error", "An error occurred while exporting order report: " + ex.getMessage()));
        }
    }
}
