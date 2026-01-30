package com.cuentabancaria.infrastructure.web;

import com.cuentabancaria.infrastructure.BaseWebTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClienteControllerTest extends BaseWebTest {

    @Autowired
    MockMvc mvc;

    @Test @DisplayName("GET /clientes devuelve los 5 clientes iniciales")
    void getTodos_devuelve5() throws Exception {
        mvc.perform(get("/clientes"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(5));
    }

    @Test @DisplayName("GET /clientes/mayores-de-edad filtra correctamente (4 adultos)")
    void getMayoresDeEdad_filtra() throws Exception {
        mvc.perform(get("/clientes/mayores-de-edad"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(4))
           .andExpect(jsonPath("$[?(@.dni=='33333333C')]").doesNotExist());
    }

    @Test @DisplayName("GET /clientes/con-cuenta-superior-a/{cantidad} devuelve los que superan la suma (2 clientes)")
    void getConCuentaSuperiorA_filtraPorSuma() throws Exception {
        mvc.perform(get("/clientes/con-cuenta-superior-a/100000"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[?(@.dni=='11111111A')]").exists())
           .andExpect(jsonPath("$[?(@.dni=='55555555E')]").exists())
           .andExpect(jsonPath("$[?(@.dni=='22222222B')]").doesNotExist());
    }

    @Test @DisplayName("GET /clientes/{dni} devuelve cliente con sus cuentas")
    void getPorDni_devuelveClienteYCuentas() throws Exception {
        mvc.perform(get("/clientes/11111111A"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.dni").value("11111111A"))
           .andExpect(jsonPath("$.cuentas.length()").value(2));
    }
}
