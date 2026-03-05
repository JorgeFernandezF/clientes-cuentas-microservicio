package com.clientescuentas.integration.controller;

import com.clientescuentas.application.service.CuentaService;
import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.infrastructure.controller.CuentaController;
import com.clientescuentas.infrastructure.dto.cuenta.ActualizarTotalRequest;
import com.clientescuentas.infrastructure.dto.cuenta.CrearCuentaRequest;
import com.clientescuentas.infrastructure.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CuentaController.class)
@Import(GlobalExceptionHandler.class)
class CuentaControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CuentaService service;

    private CuentaBancaria cuenta(Long id, String dni, String tipo, double total) {
        return CuentaBancaria.builder()
                .id(id)
                .dniCliente(dni)
                .tipoCuenta(tipo)
                .total(total)
                .build();
    }

    @Test
    @DisplayName("POST /cuentas -> 200 OK crea cuenta")
    void crear_OK() throws Exception {
        var req = new CrearCuentaRequest("111A", "NORMAL", 1200.5);

        when(service.guardar(any(CuentaBancaria.class))).thenAnswer(inv -> {
            var c = inv.getArgument(0, CuentaBancaria.class);
            return CuentaBancaria.builder()
                    .id(10L)
                    .dniCliente(c.getDniCliente())
                    .tipoCuenta(c.getTipoCuenta())
                    .total(c.getTotal())
                    .build();
        });

        mvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.dniCliente").value("111A"))
                .andExpect(jsonPath("$.total").value(1200.5));

        verify(service).guardar(any(CuentaBancaria.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("POST /cuentas -> 400 BAD REQUEST si validación falla")
    void crear_400_validation() throws Exception {
        var req = new CrearCuentaRequest("", "NORMAL", -1.0);

        mvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("GET /cuentas/{id} -> 200 OK si existe")
    void obtener_OK() throws Exception {
        when(service.buscarPorId(5L)).thenReturn(Optional.of(cuenta(5L, "111A", "NORMAL", 500.0)));

        mvc.perform(get("/cuentas/{id}", 5L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.dniCliente").value("111A"))
                .andExpect(jsonPath("$.total").value(500.0));

        verify(service).buscarPorId(5L);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /cuentas/{id} -> 404 NOT FOUND si no existe")
    void obtener_404() throws Exception {
        when(service.buscarPorId(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/cuentas/{id}", 999L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(999L);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("PUT /cuentas/{id} -> 200 OK actualiza total")
    void actualizar_OK() throws Exception {
        when(service.buscarPorId(7L)).thenReturn(Optional.of(cuenta(7L, "111A", "NORMAL", 500.0)));
        when(service.guardar(any(CuentaBancaria.class))).thenAnswer(inv -> {
            var c = inv.getArgument(0, CuentaBancaria.class);
            return CuentaBancaria.builder()
                    .id(c.getId())
                    .dniCliente(c.getDniCliente())
                    .tipoCuenta(c.getTipoCuenta())
                    .total(c.getTotal())
                    .build();
        });

        var req = new ActualizarTotalRequest(999.99);

        mvc.perform(put("/cuentas/{id}", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.total").value(999.99));

        verify(service).buscarPorId(7L);
        verify(service).guardar(any(CuentaBancaria.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("PUT /cuentas/{id} -> 404 NOT FOUND si no existe")
    void actualizar_404() throws Exception {
        when(service.buscarPorId(404L)).thenReturn(Optional.empty());

        var req = new ActualizarTotalRequest(100.0);

        mvc.perform(put("/cuentas/{id}", 404L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(404L);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("PUT /cuentas/{id} -> 400 BAD REQUEST por validación")
    void actualizar_400_validation() throws Exception {
        var req = new ActualizarTotalRequest(-5.0);

        mvc.perform(put("/cuentas/{id}", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}