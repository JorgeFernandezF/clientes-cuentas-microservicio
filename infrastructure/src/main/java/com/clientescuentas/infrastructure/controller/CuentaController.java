
package com.clientescuentas.infrastructure.controller;

import com.clientescuentas.application.service.CuentaService;
import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.infrastructure.dto.cuenta.ActualizarTotalRequest;
import com.clientescuentas.infrastructure.dto.cuenta.CrearCuentaRequest;
import com.clientescuentas.infrastructure.dto.cuenta.CuentaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceNotFoundException;
import java.util.Optional;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Cuentas", description = "Operaciones de escritura/lectura sobre cuentas bancarias")
public class CuentaController {

    private final CuentaService service;

    @Operation(summary = "Crear/registrar una cuenta")
    @PostMapping
    public CuentaDto crear(@Valid @RequestBody CrearCuentaRequest req) {
        CuentaBancaria creada = service.guardar(
                CuentaBancaria.builder()
                        .id(null)
                        .tipoCuenta(req.tipoCuenta())
                        .total(req.total())
                        .dniCliente(req.dniCliente())
                        .build()
        );
        return CuentaDto.fromDomain(creada);
    }

    @Operation(summary = "Obtener cuenta por ID")
    @GetMapping("/{id}")
    public CuentaDto obtener(@PathVariable Long id) throws InstanceNotFoundException {
        Optional<CuentaBancaria> opt = service.buscarPorId(id);
        return opt.map(CuentaDto::fromDomain)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada: " + id));
    }

    @Operation(summary = "Actualizar el total de una cuenta")
    @PutMapping("/{id}")
    public CuentaDto actualizarTotal(@PathVariable Long id, @Valid @RequestBody ActualizarTotalRequest req) throws InstanceNotFoundException {
        CuentaBancaria existente = service.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada: " + id));

        CuentaBancaria actualizada = service.guardar(
                CuentaBancaria.builder()
                        .id(existente.getId())
                        .tipoCuenta(existente.getTipoCuenta())
                        .dniCliente(existente.getDniCliente())
                        .total(req.total())
                        .build()
        );
        return CuentaDto.fromDomain(actualizada);
    }
}
