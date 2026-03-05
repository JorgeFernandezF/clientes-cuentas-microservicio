package com.clientescuentas.infrastructure.repository;

import com.clientescuentas.infrastructure.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    List<CuentaEntity> findByDniCliente(String dniCliente);

    @Query("""
        SELECT c.dniCliente
        FROM CuentaEntity c
        GROUP BY c.dniCliente
        HAVING SUM(c.total) > :cantidad
    """)
    List<String> findClientesConSaldoSuperiorA(Double cantidad);

}