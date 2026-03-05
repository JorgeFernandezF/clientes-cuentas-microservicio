package com.clientescuentas.infrastructure.repository;

import com.clientescuentas.infrastructure.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {

    @Query("""
        SELECT c FROM ClienteEntity c
        WHERE c.fechaNacimiento <= :limite
    """)
    Page<ClienteEntity> findMayoresDeEdadPaged(LocalDate limite, Pageable pageable);
}