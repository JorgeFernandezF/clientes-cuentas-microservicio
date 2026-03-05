package com.clientescuentas.infrastructure.adapter;

import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.domain.port.CuentaRepositoryPort;
import com.clientescuentas.infrastructure.entity.CuentaEntity;
import com.clientescuentas.infrastructure.repository.CuentaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaRepository jpa;

    public CuentaRepositoryAdapter(CuentaRepository jpa) {
        this.jpa = jpa;
    }

    private CuentaBancaria toDomain(CuentaEntity e) {
        return CuentaBancaria.builder()
                .id(e.getId())
                .tipoCuenta(e.getTipoCuenta())
                .total(e.getTotal())
                .dniCliente(e.getDniCliente())
                .build();
    }

    private CuentaEntity toEntity(CuentaBancaria c) {
        return CuentaEntity.builder()
                .id(c.getId())
                .tipoCuenta(c.getTipoCuenta())
                .total(c.getTotal())
                .dniCliente(c.getDniCliente())
                .build();
    }

    @Override
    public CuentaBancaria save(CuentaBancaria cuenta) {
        CuentaEntity saved = jpa.save(toEntity(cuenta));
        return toDomain(saved);
    }

    @Override
    public Optional<CuentaBancaria> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }
}