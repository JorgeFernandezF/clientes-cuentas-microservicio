package com.clientescuentas.infrastructure.adapter;

import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.domain.model.PageQuery;
import com.clientescuentas.domain.model.PageResult;
import com.clientescuentas.domain.port.ClienteRepositoryPort;
import com.clientescuentas.infrastructure.entity.ClienteEntity;
import com.clientescuentas.infrastructure.entity.CuentaEntity;
import com.clientescuentas.infrastructure.repository.ClienteRepository;
import com.clientescuentas.infrastructure.repository.CuentaRepository;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteRepository clienteJpa;
    private final CuentaRepository cuentaJpa;

    private static final java.util.Set<String> ALLOWED_SORT =
            java.util.Set.of("dni", "nombre", "apellido1", "apellido2", "fechaNacimiento");

    public ClienteRepositoryAdapter(ClienteRepository clienteJpa, CuentaRepository cuentaJpa) {
        this.clienteJpa = clienteJpa;
        this.cuentaJpa = cuentaJpa;
    }

    private Cliente toDomain(ClienteEntity entity) {
        var cuentas = cuentaJpa.findByDniCliente(entity.getDni()).stream()
                .map(this::toDomainCuenta)
                .toList();

        return Cliente.builder()
                .dni(entity.getDni())
                .nombre(entity.getNombre())
                .apellido1(entity.getApellido1())
                .apellido2(entity.getApellido2())
                .fechaNacimiento(entity.getFechaNacimiento())
                .cuentas(cuentas)
                .build();
    }

    private CuentaBancaria toDomainCuenta(CuentaEntity e) {
        return CuentaBancaria.builder()
                .id(e.getId())
                .tipoCuenta(e.getTipoCuenta())
                .total(e.getTotal())
                .dniCliente(e.getDniCliente())
                .build();
    }

    private Sort parseSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.by("dni").ascending();
        }
        String[] parts = sort.split(",", 2);
        String field = parts[0];
        String dir = parts.length > 1 ? parts[1] : "asc";

        if (!ALLOWED_SORT.contains(field)) {
            field = "dni";
            dir = "asc";
        }
        return "desc".equalsIgnoreCase(dir) ? Sort.by(field).descending() : Sort.by(field).ascending();
    }


    private Pageable pageableFrom(PageQuery query) {
        return PageRequest.of(query.page(), query.size(), parseSort(query.sort()));
    }

    @Override
    public PageResult<Cliente> findAllPaged(PageQuery query) {
        Pageable pageable = pageableFrom(query);
        Page<ClienteEntity> page = clienteJpa.findAll(pageable);

        List<Cliente> content = page.getContent().stream().map(this::toDomain).toList();

        return new PageResult<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                query.sort()
        );
    }

    @Override
    public Optional<Cliente> findByDni(String dni) {
        return clienteJpa.findById(dni).map(this::toDomain);
    }

    @Override
    public PageResult<Cliente> findMayoresDeEdadPaged(PageQuery query) {

        Pageable pageable = pageableFrom(query);
        LocalDate limite = LocalDate.now().minusYears(18);

        Page<ClienteEntity> page = clienteJpa.findMayoresDeEdadPaged(limite, pageable);

        List<Cliente> content = page.getContent().stream()
            .map(this::toDomain)
            .toList();

        return new PageResult<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            query.sort()
        );
    }

    @Override
    public PageResult<Cliente> findConSaldoSuperiorAPaged(Double cantidad, PageQuery query) {

        List<String> dnis = cuentaJpa.findClientesConSaldoSuperiorA(cantidad);

        if (dnis.isEmpty()) {
            return new PageResult<>(
                List.of(),
                query.page(),
                query.size(),
                0,
                0,
                query.sort()
            );
        }

        int from = Math.min(query.page() * query.size(), dnis.size());
        int to   = Math.min(from + query.size(), dnis.size());
        List<String> pageSlice = dnis.subList(from, to);

        List<Cliente> content = clienteJpa.findAllById(pageSlice).stream()
            .map(this::toDomain)
            .toList();
        int totalPages = (int) Math.ceil((double) dnis.size() / query.size());

        return new PageResult<>(
            content,
            query.page(),
            query.size(),
            dnis.size(),
            totalPages,
            query.sort()
        );
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = ClienteEntity.builder()
            .dni(cliente.getDni())
            .nombre(cliente.getNombre())
            .apellido1(cliente.getApellido1())
            .apellido2(cliente.getApellido2())
            .fechaNacimiento(cliente.getFechaNacimiento())
            .build();

        return toDomain(clienteJpa.save(entity));
    }
}