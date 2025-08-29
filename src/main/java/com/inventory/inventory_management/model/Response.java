package com.inventory.inventory_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model class representing a generic response structure.
 * This class is used to standardize API responses across the application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    // Logger to log information, warnings, and errors
    private static final Logger logger = LogManager.getLogger(Response.class);

    /**
     * The status of the response (e.g., "SUCCESS", "ERROR").
     */
    private String status;

    /**
     * The message providing additional details about the response.
     */
    private String message;

    /**
     * The data associated with the response (can be any object).
     */
    private Object data;

    /**
     * Logs the details of the response.
     */
    public void logResponseDetails() {
        logger.info("Response Details - Status: {}, Message: {}, Data: {}", status, message, data);
    }
}