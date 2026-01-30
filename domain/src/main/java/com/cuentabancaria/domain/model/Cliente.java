package com.cuentabancaria.domain.model;

import lombok.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private LocalDate fechaNacimiento;


    @Builder.Default
    private List<CuentaBancaria> cuentas = new ArrayList<>();

    //Se calcula la edad a partir de la fecha de nacimiento
    public int getEdad() {
        return (fechaNacimiento == null) ? 0 : Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    //Suma el total de todas las cuentas del cliente
    public double getSumaTotalCuentas() {
        return cuentas.stream().mapToDouble(c -> c.getTotal() == null ? 0.0 : c.getTotal()).sum();
    }
}


