package com.inventory.inventory_management.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StockMovement {

    // Logger to log information, warnings, and errors
    private static final Logger logger = LogManager.getLogger(StockMovement.class);

    /**
     * The unique identifier of the product.
     */
    private String productId;

    /**
     * The stock level of the product.
     */
    private String stockLevel;

    /**
     * Logs the details of the stock response.
     */

    public StockMovement(String productId, String stockLevel) {
        this.productId = productId;
        this.stockLevel = stockLevel;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getStockLevel() {
        return stockLevel;
    }
    public void setStockLevel(String stockLevel) {
        this.stockLevel = stockLevel;
    }
    /**
     * Logs the details of the stock response.
     */
    public void logStockResponseDetails() {
        logger.info("StockResponse Details - Product ID: {}, Stock Level: {}", productId, stockLevel);
    }
    }

