package com.clientescuentas.integration.controller;

import com.clientescuentas.domain.model.PageQuery;
import com.clientescuentas.domain.model.PageResult;
import com.clientescuentas.infrastructure.adapter.ClienteRepositoryAdapter;
import com.clientescuentas.infrastructure.entity.ClienteEntity;
import com.clientescuentas.infrastructure.entity.CuentaEntity;
import com.clientescuentas.infrastructure.repository.ClienteRepository;
import com.clientescuentas.infrastructure.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan(basePackages = "com.clientescuentas.infrastructure.entity")
@EnableJpaRepositories(basePackages = "com.clientescuentas.infrastructure.repository")
@Import(ClienteRepositoryAdapter.class)
class ClienteRepositoryAdapterIT {

    @Autowired private ClienteRepository clienteJpa;
    @Autowired private CuentaRepository cuentaJpa;
    @Autowired private ClienteRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        // Crea datos en H2: (usa builder o constructor que tengas)
        clienteJpa.save(ClienteEntity.builder()
                .dni("111A").nombre("Ana").apellido1("García").apellido2("Lopez")
                .fechaNacimiento(LocalDate.of(1980, 1, 1))
                .build());

        clienteJpa.save(ClienteEntity.builder()
                .dni("222B").nombre("Luis").apellido1("Santos").apellido2("Ruiz")
                .fechaNacimiento(LocalDate.of(2010, 1, 1)) // menor (ajusta según tu corte)
                .build());

        clienteJpa.save(ClienteEntity.builder()
                .dni("333C").nombre("Paco").apellido1("López").apellido2("Diaz")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build());

        cuentaJpa.save(CuentaEntity.builder().tipoCuenta("NORMAL").total(1500.0).dniCliente("111A").build());
        cuentaJpa.save(CuentaEntity.builder().tipoCuenta("AHORRO").total(500.0).dniCliente("111A").build());
        cuentaJpa.save(CuentaEntity.builder().tipoCuenta("NORMAL").total(3000.0).dniCliente("333C").build());
    }

    @Test
    @DisplayName("findAllPaged devuelve páginas correctas y contenido mapeado")
    void findAllPaged_OK() {
        PageQuery q = new PageQuery(0, 2, "dni,asc");

        PageResult<?> result = adapter.findAllPaged(q);

        assertEquals(0, result.page());
        assertEquals(2, result.size());
        assertEquals(3, result.totalElements());
        assertEquals(2, result.totalPages());

        assertEquals("111A", ((com.clientescuentas.domain.model.Cliente) result.content().get(0)).getDni());
        assertEquals("222B", ((com.clientescuentas.domain.model.Cliente) result.content().get(1)).getDni());
    }

    @Test
    @DisplayName("findMayoresDeEdadPaged filtra en BD y pagina correctamente")
    void findMayoresPaged_OK() {
        PageQuery q = new PageQuery(0, 10, "dni,asc");

        PageResult<?> result = adapter.findMayoresDeEdadPaged(q);

        assertEquals(2, result.totalElements());
        var dnis = result.content().stream()
                .map(c -> ((com.clientescuentas.domain.model.Cliente) c).getDni())
                .toList();
        assertTrue(dnis.containsAll(List.of("111A", "333C")));
    }

    @Test
    @DisplayName("findConSaldoSuperiorAPaged devuelve solo los clientes cuyo SUM(total) > cantidad")
    void findSaldoSuperiorPaged_OK() {
        PageQuery q = new PageQuery(0, 10, "dni,asc");

        PageResult<?> result = adapter.findConSaldoSuperiorAPaged(1000.0, q);

        var dnis = result.content().stream()
                .map(c -> ((com.clientescuentas.domain.model.Cliente) c).getDni())
                .toList();
        assertEquals(2, result.totalElements());
        assertTrue(dnis.containsAll(List.of("111A", "333C")));
    }
}