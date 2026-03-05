package com.clientescuentas.domain.port;

import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.PageQuery;
import com.clientescuentas.domain.model.PageResult;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Optional<Cliente> findByDni(String dni);

    Cliente save(Cliente cliente);

    PageResult<Cliente> findAllPaged(PageQuery query);

    PageResult<Cliente> findMayoresDeEdadPaged(PageQuery query);

    PageResult<Cliente> findConSaldoSuperiorAPaged(Double cantidad, PageQuery query);

}