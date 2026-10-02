package com.lecoyo.riscvbackend.api.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(OutOfMemoryError.class)
    public ResponseEntity<String> handleOOMException(OutOfMemoryError e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Simulation stalled, infinite loop. JVM Error: \n" + e.getMessage());
    }
}
