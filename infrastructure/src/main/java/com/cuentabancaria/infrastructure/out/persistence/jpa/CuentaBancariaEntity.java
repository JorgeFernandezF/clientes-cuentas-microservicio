package com.cuentabancaria.infrastructure.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuentas_bancarias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CuentaBancariaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_cuenta", nullable = false)
    private String tipoCuenta;

    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_cliente", nullable = false)
    private ClienteEntity cliente;
}
