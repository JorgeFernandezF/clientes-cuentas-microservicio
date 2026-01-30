package com.cuentabancaria.infrastructure.out.persistence.repo;

import com.cuentabancaria.infrastructure.out.persistence.jpa.CuentaBancariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaBancariaJpaRepository extends JpaRepository<CuentaBancariaEntity, Long> {}
