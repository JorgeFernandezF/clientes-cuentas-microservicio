package com.cuentabancaria.application.out;

import com.cuentabancaria.domain.model.Cliente;
import java.util.*;

public interface ClienteRepositoryPort {
    List<Cliente> findAllWithCuentas();
    Optional<Cliente> findByDniWithCuentas(String dni);
    Cliente save(Cliente cliente);
}