
package com.clientescuentas.domain.port;
import com.clientescuentas.domain.model.CuentaBancaria;
import java.util.*;

public interface CuentaRepositoryPort {
    CuentaBancaria save(CuentaBancaria cuenta);
    Optional<CuentaBancaria> findById(Long id);
}
