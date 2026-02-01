package com.cuentabancaria.infrastructure.in.web;

import com.cuentabancaria.application.in.*;
import com.cuentabancaria.infrastructure.in.web.dto.ClienteDto;
import com.cuentabancaria.infrastructure.in.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private final ObtenerClientesUseCase obtenerTodos;
    private final ObtenerMayoresDeEdadUseCase mayores;
    private final ObtenerClientesConSaldoSuperiorUseCase saldoSuperior;
    private final ObtenerClientePorDniUseCase porDni;

    //Obtiene todos los clientes
    @GetMapping
    public ResponseEntity<List<ClienteDto>> getTodos() {
        return ResponseEntity.ok(
                obtenerTodos.handle().stream().map(DtoMapper::toDto).toList());
    }

    //Obtiene los clientes mayores de edad
    @GetMapping("/mayores-de-edad")
    public ResponseEntity<List<ClienteDto>> getMayores() {
        return ResponseEntity.ok(
                mayores.handleMayores().stream().map(DtoMapper::toDto).toList());
    }

    //Obtiene los clientes con saldo mayor al indicado
    @GetMapping("/con-cuenta-superior-a/{cantidad}")
    public ResponseEntity<List<ClienteDto>> getConSaldo(@PathVariable("cantidad") double cantidad) {
        return ResponseEntity.ok(
                saldoSuperior.handle(cantidad).stream().map(DtoMapper::toDto).toList());
    }

    //Obtiene un cliente buscando por su dni
    @GetMapping("/{dni}")
    public ResponseEntity<ClienteDto> getPorDni(@PathVariable("dni") String dni) throws InstanceNotFoundException {
        return ResponseEntity.ok(DtoMapper.toDto(porDni.handle(dni)));
    }
}
