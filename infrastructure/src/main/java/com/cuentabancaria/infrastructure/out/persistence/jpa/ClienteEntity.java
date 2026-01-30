package com.cuentabancaria.infrastructure.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteEntity {
    @Id
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private LocalDate fechaNacimiento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CuentaBancariaEntity> cuentas = new ArrayList<>();
}
