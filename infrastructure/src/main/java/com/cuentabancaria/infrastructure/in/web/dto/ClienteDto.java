package com.cuentabancaria.infrastructure.in.web.dto;

import java.time.LocalDate;
import java.util.List;

public record ClienteDto(
        String dni,
        String nombre,
        String apellido1,
        String apellido2,
        LocalDate fechaNacimiento,
        List<CuentaDto> cuentas
) {}