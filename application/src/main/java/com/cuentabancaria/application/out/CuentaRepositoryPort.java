package com.cuentabancaria.application.out;

import com.cuentabancaria.domain.model.CuentaBancaria;
import java.util.*;

public interface CuentaRepositoryPort {
    CuentaBancaria save(CuentaBancaria cuenta);
    Optional<CuentaBancaria> findById(Long id);
}
