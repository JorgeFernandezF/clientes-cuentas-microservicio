package com.clientescuentas.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuentas_bancarias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoCuenta;

    private Double total;

    @Column(name = "dni_cliente")
    private String dniCliente;
}