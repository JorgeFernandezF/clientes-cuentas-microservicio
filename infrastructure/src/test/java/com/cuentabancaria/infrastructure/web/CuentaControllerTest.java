package com.cuentabancaria.infrastructure.web;

import com.cuentabancaria.infrastructure.BaseWebTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CuentaControllerTest extends BaseWebTest {

    @Autowired
    MockMvc mvc;

    ObjectMapper om = new ObjectMapper();

    @Test @DisplayName("POST /cuentas crea cuenta para cliente existente")
    void post_crea_para_cliente_existente() throws Exception {
        mvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "dniCliente", "11111111A",
                        "tipoCuenta", "NORMAL",
                        "total", 50000
                ))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.dniCliente").value("11111111A"))
           .andExpect(jsonPath("$.tipoCuenta").value("NORMAL"))
           .andExpect(jsonPath("$.total").value(50000.0));
    }

    @Test @DisplayName("POST /cuentas crea cliente nuevo si no existe")
    void post_crea_cliente_nuevo_si_no_existe() throws Exception {
        mvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of(
                        "dniCliente", "99999999Z",
                        "tipoCuenta", "PREMIUM",
                        "total", 1000
                ))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.dniCliente").value("99999999Z"))
           .andExpect(jsonPath("$.tipoCuenta").value("PREMIUM"))
           .andExpect(jsonPath("$.total").value(1000.0));
    }

    @Test @DisplayName("PUT /cuentas/{id} actualiza el saldo")
    void put_actualiza_saldo() throws Exception {
        mvc.perform(put("/cuentas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("total", 180000))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.total").value(180000.0));
    }
}
