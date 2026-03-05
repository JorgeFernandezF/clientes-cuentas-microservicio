
package com.clientescuentas.infrastructure.dto.cuenta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ActualizarTotalRequest(
        @NotNull
        @PositiveOrZero Double total
) { }
