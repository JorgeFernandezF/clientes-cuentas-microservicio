package com.cuentabancaria.application.in;

import com.cuentabancaria.domain.model.Cliente;
import java.util.List;

public interface ObtenerClientesUseCase {
    List<Cliente> handle();
}