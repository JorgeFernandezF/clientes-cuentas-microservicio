package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.CuentaBancaria;

public interface CrearCuentaUseCase {
    CuentaBancaria crear(String dniCliente, String tipoCuenta, Double total);
}