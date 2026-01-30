package com.cuentabancaria.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaBancaria {

    private Long id;
    private String tipoCuenta;
    private Double total;
    private String dniCliente; //Dni del cliente asociado a la cuenta
}