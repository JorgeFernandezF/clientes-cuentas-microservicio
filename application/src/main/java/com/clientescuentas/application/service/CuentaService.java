package com.clientescuentas.application.service;

import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.domain.port.ClienteRepositoryPort;
import com.clientescuentas.domain.port.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepositoryPort cuentas;
    private final ClienteRepositoryPort clientes;

    @Transactional
    public CuentaBancaria guardar(CuentaBancaria cuenta) {

        clientes.findByDni(cuenta.getDniCliente())
            .orElseGet(() -> {
                Cliente nuevo = Cliente.builder()
                    .dni(cuenta.getDniCliente())
                    .nombre("N/A")
                    .apellido1("N/A")
                    .apellido2("N/A")
                    .fechaNacimiento(LocalDate.now())
                    .build();
                return clientes.save(nuevo);
            });

        return cuentas.save(cuenta);
    }

    public Optional<CuentaBancaria> buscarPorId(Long id) throws InstanceNotFoundException {
        return Optional.of(cuentas.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("Cuenta no encontrada: " + id)));
    }
}