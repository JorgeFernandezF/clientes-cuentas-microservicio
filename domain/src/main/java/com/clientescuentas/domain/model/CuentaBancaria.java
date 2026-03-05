
package com.clientescuentas.domain.model;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CuentaBancaria {
    private Long id;
    private String tipoCuenta;
    private Double total;
    private String dniCliente;
}
