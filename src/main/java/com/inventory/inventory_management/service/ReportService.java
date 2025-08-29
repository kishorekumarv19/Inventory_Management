package com.inventory.inventory_management.service;

import com.inventory.inventory_management.entities.Order;
import com.inventory.inventory_management.entities.Product;
import com.inventory.inventory_management.repository.OrderRepository;
import com.inventory.inventory_management.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * Service class for generating reports.
 */
@Service
public class ReportService {

    // Logger instance for logging service operations
    private static final Logger logger = LogManager.getLogger(ReportService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Retrieves the inventory report.
     *
     * @return a list of all products
     */
    public List<Product> getInventoryReport() {
        try {
            return productRepository.findAll();
        } catch (Exception ex) {
            logger.error("An error occurred while fetching inventory report: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Retrieves the order report.
     *
     * @return a list of all orders
     */
    public List<Order> getOrderReport() {
        try {
            return orderRepository.findAll();
        } catch (Exception ex) {
            logger.error("An error occurred while fetching order report: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Exports the inventory report to an Excel file.
     *
     * @return the file path of the generated Excel report
     * @throws IOException if an I/O error occurs
     */
    public String exportInventoryReportToExcel() throws IOException {
        try {
            List<Product> products = productRepository.findAll();
            String directoryPath = "reports";
            File directory = new File(directoryPath);

            // Ensure the directory exists
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Unable to create report directory");
            }

            String filePath = directoryPath + "/inventory_report.xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Inventory Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Price");
            headerRow.createCell(4).setCellValue("Stock Quantity");

            // Populate data rows
            int rowNum = 1;
            if (!products.isEmpty()) {
                for (Product product : products) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(product.getId());
                    row.createCell(1).setCellValue(product.getName());
                    row.createCell(2).setCellValue(product.getDescription());
                    row.createCell(3).setCellValue(product.getPrice());
                    row.createCell(4).setCellValue(product.getQuantity());
                }
            }

            // Write the workbook to the file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            } finally {
                workbook.close();
            }

            return filePath;
        } catch (IOException ex) {
            logger.error("An error occurred while exporting inventory report to Excel: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Exports the order report to an Excel file.
     *
     * @return the file path of the generated Excel report
     * @throws IOException if an I/O error occurs
     */
    public String exportOrderReportToExcel() throws IOException {
        try {
            List<Order> orders = orderRepository.findAll();
            String directoryPath = "reports";
            File directory = new File(directoryPath);

            // Ensure the directory exists
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Unable to create report directory");
            }

            String filePath = directoryPath + "/order_report.xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Order Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Quantity");
            headerRow.createCell(2).setCellValue("Total Price");
            headerRow.createCell(3).setCellValue("Status");
            headerRow.createCell(4).setCellValue("Created At");

            // Populate data rows
            int rowNum = 1;
            if (!orders.isEmpty()) {
                for (Order order : orders) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(order.getId());
                    row.createCell(1).setCellValue(order.getQuantity());
                    row.createCell(2).setCellValue(order.getTotalPrice());
                    row.createCell(3).setCellValue(order.getStatus());
                    row.createCell(4).setCellValue(order.getCreatedAt().toString());
                }
            }

            // Write the workbook to the file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            } finally {
                workbook.close();
            }

            return filePath;
        } catch (IOException ex) {
            logger.error("An error occurred while exporting order report to Excel: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}