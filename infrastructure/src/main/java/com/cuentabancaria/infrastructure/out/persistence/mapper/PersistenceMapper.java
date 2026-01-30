package com.cuentabancaria.infrastructure.out.persistence.mapper;

import com.cuentabancaria.domain.model.*;
import com.cuentabancaria.infrastructure.out.persistence.jpa.*;

import java.util.List;

public class PersistenceMapper {

    public static Cliente toDomain(ClienteEntity e) {
        if (e == null) return null;

        Cliente c = Cliente.builder()
                .dni(e.getDni())
                .nombre(e.getNombre())
                .apellido1(e.getApellido1())
                .apellido2(e.getApellido2())
                .fechaNacimiento(e.getFechaNacimiento())
                .build();

        if (e.getCuentas() != null) {
            List<CuentaBancaria> cuentas = e.getCuentas()
                    .stream()
                    .map(PersistenceMapper::toDomainCuentaSinCliente)
                    .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

            c.setCuentas(cuentas);
        }

        return c;
    }

    public static CuentaBancaria toDomainCuentaSinCliente(CuentaBancariaEntity e) {
        if (e == null) return null;

        return CuentaBancaria.builder()
                .id(e.getId())
                .tipoCuenta(e.getTipoCuenta())
                .total(e.getTotal())
                .build();
    }

    public static ClienteEntity toEntity(Cliente d) {
        if (d == null) return null;

        ClienteEntity e = ClienteEntity.builder()
                .dni(d.getDni())
                .nombre(d.getNombre())
                .apellido1(d.getApellido1())
                .apellido2(d.getApellido2())
                .fechaNacimiento(d.getFechaNacimiento())
                .build();

        if (d.getCuentas() != null) {
            var cuentas = d.getCuentas().stream()
                    .map(cc -> {
                        CuentaBancariaEntity ce = toEntityCuentaSinCliente(cc);
                        ce.setCliente(e);
                        return ce;
                    })
                    .toList();

            e.setCuentas(new java.util.ArrayList<>(cuentas));
        }

        return e;
    }

    public static CuentaBancariaEntity toEntityCuentaSinCliente(CuentaBancaria d) {
        if (d == null) return null;

        return CuentaBancariaEntity.builder()
                .id(d.getId())
                .tipoCuenta(d.getTipoCuenta())
                .total(d.getTotal())
                .build();
    }

    public static CuentaBancaria toDomain(CuentaBancariaEntity e) {
        if (e == null) return null;
        return CuentaBancaria.builder()
                .id(e.getId())
                .tipoCuenta(e.getTipoCuenta())
                .total(e.getTotal())
                .dniCliente(e.getCliente() != null ? e.getCliente().getDni() : null)
                .build();
    }

    public static CuentaBancariaEntity toEntity(CuentaBancaria d) {
        return toEntityCuentaSinCliente(d);
    }

}
