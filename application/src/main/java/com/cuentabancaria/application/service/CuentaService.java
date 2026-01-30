package com.cuentabancaria.application.service;

import com.cuentabancaria.application.in.*;
import com.cuentabancaria.application.out.ClienteRepositoryPort;
import com.cuentabancaria.application.out.CuentaRepositoryPort;
import com.cuentabancaria.domain.model.Cliente;
import com.cuentabancaria.domain.model.CuentaBancaria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuentaService implements CrearCuentaUseCase, ActualizarSaldoCuentaUseCase {

    private final ClienteRepositoryPort clientes;
    private final CuentaRepositoryPort cuentas;

    //Crea una cuenta nueva
    @Override
    public CuentaBancaria crear(String dniCliente, String tipoCuenta, Double total) {
        //Comprobación de si existe el cliente, si no lo crea con datos vacios
        Cliente cliente = clientes.findByDniWithCuentas(dniCliente)
                .orElseGet(() -> clientes.save(
                        Cliente.builder()
                                .dni(dniCliente)
                                .nombre("N/A")
                                .apellido1("N/A")
                                .apellido2("N/A")
                                .build()
                ));

        CuentaBancaria nueva = CuentaBancaria.builder()
                .tipoCuenta(tipoCuenta)
                .total(total)
                .dniCliente(dniCliente)
                .build();

        cliente.getCuentas().add(nueva);

        return cuentas.save(nueva);
    }

    //Actualiza la cantidad total en una cuenta dada
    @Override
    public CuentaBancaria actualizar(Long idCuenta, Double total) {
        CuentaBancaria c = cuentas.findById(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + idCuenta));
        c.setTotal(total);
        return cuentas.save(c);
    }
}