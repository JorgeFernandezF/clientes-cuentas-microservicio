package com.cuentabancaria.infrastructure.out.persistence.repo;

import com.cuentabancaria.infrastructure.out.persistence.jpa.ClienteEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, String> {
    @Override
    @EntityGraph(attributePaths = "cuentas")
    List<ClienteEntity> findAll();

    @EntityGraph(attributePaths = "cuentas")
    Optional<ClienteEntity> findByDni(String dni);
}
