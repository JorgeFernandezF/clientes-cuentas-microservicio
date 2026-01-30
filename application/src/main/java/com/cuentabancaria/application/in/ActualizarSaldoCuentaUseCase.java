package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.CuentaBancaria;

public interface ActualizarSaldoCuentaUseCase {
    CuentaBancaria actualizar(Long idCuenta, Double total);
}
