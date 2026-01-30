package com.cuentabancaria.infrastructure.in.web.dto;

public record CuentaDto(
        Long id,
        String tipoCuenta,
        Double total,
        String dniCliente
) {}