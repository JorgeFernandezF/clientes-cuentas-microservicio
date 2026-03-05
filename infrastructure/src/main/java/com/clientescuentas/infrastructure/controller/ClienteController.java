package com.clientescuentas.infrastructure.controller;

import com.clientescuentas.application.service.ClienteService;
import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.PageResult;
import com.clientescuentas.infrastructure.dto.cliente.ClienteDto;
import com.clientescuentas.infrastructure.dto.common.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Clientes", description = "Operaciones de lectura sobre clientes")
public class ClienteController {


    private final ClienteService service;

    @Operation(summary = "Listar clientes (paginado)")
    @GetMapping
    public PageDto<ClienteDto> listar(Pageable pageable) {
        String sort = pageable.getSort().stream()
                .findFirst()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .orElse("dni,asc");

        var pageResult = service.listar(pageable.getPageNumber(), pageable.getPageSize(), sort);

        List<ClienteDto> content = pageResult.content()
                .stream()
                .map(ClienteDto::fromDomain)
                .toList();

        return new PageDto<>(
                content,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.sort()
        );
    }

    @Operation(summary = "Obtener cliente por DNI")
    @GetMapping("/{dni}")
    public ClienteDto porDni(@PathVariable String dni) throws InstanceNotFoundException {
        Cliente cliente = service.porDni(dni); // lanza InstanceNotFoundException si no existe
        return ClienteDto.fromDomain(cliente);
    }

    @Operation(summary = "Listar clientes mayores de edad (paginado )")
    @GetMapping("/mayores-de-edad")
    public PageDto<ClienteDto> mayoresDeEdad(Pageable pageable) {
        final String sort = pageable.getSort().stream()
                .findFirst()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .orElse("dni,asc");

        PageResult<Cliente> pageResult =
                service.mayoresPaged(pageable.getPageNumber(), pageable.getPageSize(), sort);

        List<ClienteDto> content = pageResult.content().stream().map(ClienteDto::fromDomain).toList();

        return new PageDto<>(
                content,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.sort()
        );
    }

    @Operation(summary = "Listar clientes con suma de cuentas superior a una cantidad (paginado)")
    @GetMapping("/con-cuenta-superior-a")
    public PageDto<ClienteDto> conSaldoSuperiorA(
            @Parameter(description = "Cantidad mínima a superar (≥ 0)")
            @RequestParam(name = "cantidad")
            @PositiveOrZero Double cantidad,
            Pageable pageable) {

        final String sort = pageable.getSort().stream()
                .findFirst()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .orElse("dni,asc");

        PageResult<Cliente> pageResult =
                service.conSaldoSuperiorPaged(cantidad, pageable.getPageNumber(), pageable.getPageSize(), sort);

        List<ClienteDto> content = pageResult.content().stream().map(ClienteDto::fromDomain).toList();

        return new PageDto<>(
                content,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.sort()
        );
    }
}
