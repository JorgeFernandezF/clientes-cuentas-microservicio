package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.Cliente;

public interface ObtenerClientePorDniUseCase {
    Cliente handle(String dni);
}