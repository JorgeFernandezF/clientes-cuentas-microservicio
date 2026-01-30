package com.cuentabancaria.infrastructure.in.web;

import com.cuentabancaria.application.in.*;
import com.cuentabancaria.infrastructure.in.web.dto.CuentaDto;
import com.cuentabancaria.infrastructure.in.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cuentas")
public class CuentaController {

    private final CrearCuentaUseCase crearCuenta;
    private final ActualizarSaldoCuentaUseCase actualizar;

    public record AltaRequest(String dniCliente, String tipoCuenta, Double total) {}
    public record ActualizarRequest(Double total) {}

    //Crea una cuenta nueva
    @PostMapping
    public ResponseEntity<CuentaDto> crear(@RequestBody AltaRequest r) {
        return ResponseEntity.ok(DtoMapper.toDto(
                crearCuenta.crear(r.dniCliente(), r.tipoCuenta(), r.total())));
    }

    //Actualiza una cuenta a una cantidad dada
    @PutMapping("/{id}")
    public ResponseEntity<CuentaDto> actualizar(@PathVariable("id") Long id, @RequestBody ActualizarRequest r) {
        return ResponseEntity.ok(DtoMapper.toDto(actualizar.actualizar(id, r.total())));
    }
}