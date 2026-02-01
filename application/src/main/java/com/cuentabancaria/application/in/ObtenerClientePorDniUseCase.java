package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.Cliente;

import javax.management.InstanceNotFoundException;

public interface ObtenerClientePorDniUseCase {
    Cliente handle(String dni) throws InstanceNotFoundException;
}