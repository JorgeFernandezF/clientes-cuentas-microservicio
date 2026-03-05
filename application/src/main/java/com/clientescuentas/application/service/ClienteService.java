package com.clientescuentas.application.service;

import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.PageQuery;
import com.clientescuentas.domain.model.PageResult;
import com.clientescuentas.domain.port.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepositoryPort repo;

    public PageResult<Cliente> listar(int page, int size, String sort) {
        return repo.findAllPaged(new PageQuery(page, size, sort));
    }

    public Cliente porDni(String dni) throws InstanceNotFoundException {
        return repo.findByDni(dni)
                .orElseThrow(() -> new InstanceNotFoundException("Cliente no encontrado: " + dni));
    }

    public PageResult<Cliente> mayoresPaged(int page, int size, String sort) {
        return repo.findMayoresDeEdadPaged(new PageQuery(page, size, sort));
    }

    public PageResult<Cliente> conSaldoSuperiorPaged(Double cantidad, int page, int size, String sort) {
        return repo.findConSaldoSuperiorAPaged(cantidad, new PageQuery(page, size, sort));
    }
}