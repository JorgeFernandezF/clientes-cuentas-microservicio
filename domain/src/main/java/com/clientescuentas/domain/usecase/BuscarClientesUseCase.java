package com.clientescuentas.domain.usecase;

import com.clientescuentas.domain.model.Cliente;
import java.util.List;

public interface BuscarClientesUseCase {
    List<Cliente> listar(int page, int size, String sort);
}