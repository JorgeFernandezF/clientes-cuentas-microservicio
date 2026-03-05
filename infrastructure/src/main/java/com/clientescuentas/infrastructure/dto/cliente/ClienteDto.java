
package com.clientescuentas.infrastructure.dto.cliente;

import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.infrastructure.dto.cuenta.CuentaDto;

import java.util.List;

public record ClienteDto(
        String dni,
        String nombre,
        String apellido1,
        String apellido2,
        String fechaNacimiento,
        List<CuentaDto> cuentas
) {
    public static ClienteDto fromDomain(Cliente c) {
        List<CuentaDto> cuentas = c.getCuentas() == null ? List.of()
                : c.getCuentas().stream().map(CuentaDto::fromDomain).toList();
        return new ClienteDto(
                c.getDni(),
                c.getNombre(),
                c.getApellido1(),
                c.getApellido2(),
                c.getFechaNacimiento() == null ? null : c.getFechaNacimiento().toString(),
                cuentas
        );
    }
}
