package com.clientescuentas.integration.controller;

import com.clientescuentas.application.service.ClienteService;
import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.domain.model.PageResult;
import com.clientescuentas.infrastructure.controller.ClienteController;
import com.clientescuentas.infrastructure.web.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClienteController.class)
@Import(GlobalExceptionHandler.class)
class ClienteControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private ClienteService service;

    private Cliente cliente(String dni, double... totales) {
        var cuentas = java.util.Arrays.stream(totales)
                .mapToObj(t -> CuentaBancaria.builder()
                        .id(null).tipoCuenta("NORMAL").total(t).dniCliente(dni)
                        .build())
                .toList();

        return Cliente.builder()
                .dni(dni).nombre("Nombre").apellido1("Ape1").apellido2("Ape2")
                .fechaNacimiento(LocalDate.of(1990,1,1))
                .cuentas(cuentas)
                .build();
    }

    private PageResult<Cliente> pageResult(List<Cliente> content, int page, int size, long total, int totalPages, String sort) {
        return new PageResult<>(content, page, size, total, totalPages, sort);
    }

    @Test
    @DisplayName("GET /clientes/{dni} -> 200 OK")
    void porDni_OK() throws Exception {
        when(service.porDni("111A")).thenReturn(cliente("111A", 1000));

        mvc.perform(get("/clientes/{dni}", "111A").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("111A"))
                .andExpect(jsonPath("$.cuentas", hasSize(1)));

        verify(service).porDni("111A");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /clientes/{dni} -> 404 NOT FOUND (InstanceNotFoundException)")
    void porDni_404() throws Exception {
        when(service.porDni("999999")).thenThrow(new InstanceNotFoundException("Cliente no encontrado: 999999"));

        mvc.perform(get("/clientes/{dni}", "999999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", containsString("Cliente no encontrado")));

        verify(service).porDni("999999");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /clientes -> 200 con PageDto")
    void listar_paginado_OK() throws Exception {
        var c1 = cliente("111A", 1000, 500);
        var c2 = cliente("222B");
        when(service.listar(0, 2, "dni,asc"))
                .thenReturn(pageResult(List.of(c1, c2), 0, 2, 5, 3, "dni,asc"));

        mvc.perform(get("/clientes")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "dni,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].dni").value("111A"))
                .andExpect(jsonPath("$.content[1].dni").value("222B"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.sort").value("dni,asc"));

        verify(service).listar(0, 2, "dni,asc");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /clientes/mayores-de-edad -> 200 con PageDto")
    void mayoresDeEdad_paginado_OK() throws Exception {
        var c1 = cliente("111A");
        var c2 = cliente("333C");
        when(service.mayoresPaged(0, 10, "dni,asc"))
                .thenReturn(pageResult(List.of(c1, c2), 0, 10, 2, 1, "dni,asc"));

        mvc.perform(get("/clientes/mayores-de-edad")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dni,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page").value(0));

        verify(service).mayoresPaged(0, 10, "dni,asc");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /clientes/con-cuenta-superior-a -> 200 con PageDto")
    void saldoSuperior_paginado_OK() throws Exception {
        var c1 = cliente("111A", 2000);
        when(service.conSaldoSuperiorPaged(1000.0, 0, 10, "dni,asc"))
                .thenReturn(pageResult(List.of(c1), 0, 10, 1, 1, "dni,asc"));

        mvc.perform(get("/clientes/con-cuenta-superior-a")
                        .param("cantidad", "1000")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dni,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value("111A"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service).conSaldoSuperiorPaged(1000.0, 0, 10, "dni,asc");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /clientes/con-cuenta-superior-a -> 400 BAD REQUEST si cantidad < 0")
    void saldoSuperior_400_validation() throws Exception {
        mvc.perform(get("/clientes/con-cuenta-superior-a")
                        .param("cantidad", "-1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dni,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}