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
class PedidoITTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarPedidos_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    void crearPedido_valido_devuelve201YCalculaTotal() throws Exception {
        String body = """
                {
                  "idCliente": 1,
                  "tipoEntrega": "DESPACHO_DOMICILIO",
                  "estado": "PENDIENTE_PAGO",
                  "detalles": [
                    {
                      "idProducto": 1,
                      "cantidad": 2,
                      "precioUnitario": 15000
                    },
                    {
                      "idProducto": 5,
                      "cantidad": 1,
                      "precioUnitario": 30000
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPedido", notNullValue()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE_PAGO"))
                .andExpect(jsonPath("$.tipoEntrega").value("DESPACHO_DOMICILIO"))
                .andExpect(jsonPath("$.total").value(60000));
    }

    @Test
    void buscarPedidoNoExistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPedidosPorCliente_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos/cliente/1"))
                .andExpect(status().isOk());
    }
}