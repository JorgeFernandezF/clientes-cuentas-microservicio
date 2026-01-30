package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.Cliente;
import java.util.List;

public interface ObtenerClientesConSaldoSuperiorUseCase {
    List<Cliente> handle(double cantidad);
}