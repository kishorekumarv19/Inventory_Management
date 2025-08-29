package com.inventory.inventory_management.exception;

import com.inventory.inventory_management.model.Response;
import com.inventory.inventory_management.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions.
     *
     * @param ex the MethodArgumentNotValidException
     * @return a response entity with the error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Log the validation exception
        logger.error("Validation exception occurred", ex);

        // Extract the error message from the exception
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

        // Return a bad request response with the error message
        return ResponseEntity.badRequest().body(new Response(Constants.ERROR, Constants.INVALID_INPUT, errorMessage));
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex the Exception
     * @return a response entity with the error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleAllExceptions(Exception ex) {
        // Log the exception
        logger.error("An unexpected error occurred", ex);

        // Return an internal server error response with the error message
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(Constants.ERROR, "Internal Server Error", ex.getMessage()));
    }
}