package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.CuentaBancaria;

import javax.management.InstanceNotFoundException;

public interface ActualizarSaldoCuentaUseCase {
    CuentaBancaria actualizar(Long idCuenta, Double total) throws InstanceNotFoundException;
}
