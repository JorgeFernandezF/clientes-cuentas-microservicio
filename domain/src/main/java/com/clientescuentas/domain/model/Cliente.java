
package com.clientescuentas.domain.model;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Cliente {
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private LocalDate fechaNacimiento;
    private List<CuentaBancaria> cuentas;
}
