package com.clientescuentas.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.InstanceNotFoundException;
import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InstanceNotFoundException.class)
    public ProblemDetail handleInstanceNotFound(
            InstanceNotFoundException ex,
            HttpServletRequest request
    ) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Recurso no encontrado");
        pd.setType(URI.create("https://errors.clientes-cuentas.com/not-found"));

        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("path", request.getRequestURI());

        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Petición inválida");
        pd.setTitle("Error de validación");
        pd.setType(URI.create("https://errors.clientes-cuentas.com/validation"));

        pd.setProperty("errors", ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList());

        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("path", request.getRequestURI());

        return pd;
    }
}