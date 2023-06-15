package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class CustomExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class.getName());

    @ExceptionHandler(com.fasterxml.jackson.core.JsonParseException.class)
    public ResponseEntity<Object> handleJsonParseException(com.fasterxml.jackson.core.JsonParseException ex) {
        // Xử lý lỗi và thông báo tại đây
        LOGGER.error("Error parsing JSON: " + ex.getMessage());

        // Trả về một ResponseEntity không hợp lệ (bad request) với thông báo lỗi
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format.");
    }
}


