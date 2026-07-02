package com.perfulandia.ms_envios;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CuponITTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarCupones_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isOk());
    }

    @Test
    void crearCupon_valido_devuelve201() throws Exception {
        String body = """
                {
                  "codigo": "VERANO10",
                  "fechaInicio": "2026-01-01T00:00:00",
                  "fechaFin": "2026-12-31T23:59:59",
                  "tipoDescuento": "PORCENTAJE",
                  "valorDescuento": 10,
                  "cantidadMaximaUsos": 100,
                  "usosDisponibles": 100,
                  "activo": true
                }
                """;

        mockMvc.perform(post("/api/v1/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCupon", notNullValue()))
                .andExpect(jsonPath("$.codigo").value("VERANO10"))
                .andExpect(jsonPath("$.tipoDescuento").value("PORCENTAJE"))
                .andExpect(jsonPath("$.valorDescuento").value(10));
    }

    @Test
    void buscarCuponNoExistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/v1/cupones/999999"))
                .andExpect(status().isNotFound());
    }
}