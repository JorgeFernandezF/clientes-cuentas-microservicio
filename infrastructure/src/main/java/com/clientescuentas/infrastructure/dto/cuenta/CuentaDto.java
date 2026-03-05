
package com.clientescuentas.infrastructure.dto.cuenta;

import com.clientescuentas.domain.model.CuentaBancaria;

public record CuentaDto(
        Long id,
        String tipoCuenta,
        Double total,
        String dniCliente
) {
    public static CuentaDto fromDomain(CuentaBancaria cb) {
        return new CuentaDto(
                cb.getId(),
                cb.getTipoCuenta(),
                cb.getTotal(),
                cb.getDniCliente()
        );
    }
}
