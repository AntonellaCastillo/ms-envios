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
class EnvioITTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarEnvios_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/envios"))
                .andExpect(status().isOk());
    }

    @Test
    void crearEnvio_valido_devuelve201() throws Exception {
        String body = """
                {
                  "idPedido": 1,
                  "idSucursal": 1,
                  "direccionDestino": "Av. Siempre Viva 742, Santiago",
                  "estado": "EN_BODEGA"
                }
                """;

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEnvio", notNullValue()))
                .andExpect(jsonPath("$.tracking", notNullValue()))
                .andExpect(jsonPath("$.estado").value("EN_BODEGA"))
                .andExpect(jsonPath("$.direccionDestino").value("Av. Siempre Viva 742, Santiago"));
    }

    @Test
    void buscarEnvioNoExistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/v1/envios/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rastrearEnvioNoExistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/v1/envios/tracking/TRK-NOEXISTE"))
                .andExpect(status().isNotFound());
    }
}