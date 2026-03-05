
package com.clientescuentas.infrastructure.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CrearCuentaRequest(
        @NotBlank String dniCliente,
        @NotBlank String tipoCuenta,
        @NotNull
        @PositiveOrZero Double total
) { }
