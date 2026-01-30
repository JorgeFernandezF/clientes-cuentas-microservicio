package com.cuentabancaria.infrastructure.in.web.mapper;

import com.cuentabancaria.domain.model.Cliente;
import com.cuentabancaria.domain.model.CuentaBancaria;
import com.cuentabancaria.infrastructure.in.web.dto.ClienteDto;
import com.cuentabancaria.infrastructure.in.web.dto.CuentaDto;

import java.util.List;

public class DtoMapper {
    public static ClienteDto toDto(Cliente c) {
        List<CuentaDto> cuentas = c.getCuentas().stream()
                .map(cb -> toDto(cb, c.getDni()))
                .toList();
        return new ClienteDto(c.getDni(), c.getNombre(), c.getApellido1(), c.getApellido2(),
                c.getFechaNacimiento(), cuentas);
    }

    public static CuentaDto toDto(CuentaBancaria cb) {
        return new CuentaDto(cb.getId(), cb.getTipoCuenta(), cb.getTotal(), cb.getDniCliente());
    }


    public static CuentaDto toDto(CuentaBancaria cb, String dniCliente) {
        return new CuentaDto(cb.getId(), cb.getTipoCuenta(), cb.getTotal(), dniCliente);
    }
}