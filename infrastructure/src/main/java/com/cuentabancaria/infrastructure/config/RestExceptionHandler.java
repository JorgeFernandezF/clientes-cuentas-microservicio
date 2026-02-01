package com.cuentabancaria.infrastructure.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.InstanceNotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(InstanceNotFoundException.class)
    ResponseEntity<?> handleInstanceNotFound(InstanceNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}