package com.perfulandia.ms_envios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoClienteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PagoClienteService pagoClienteService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                pagoClienteService,
                "urlPagos",
                "http://localhost:8086"
        );
    }

    @Test
    void solicitarReembolso_llamaRestTemplatePut() {
        Long idPedido = 1L;
        String url = "http://localhost:8086/api/v1/pagos/pedido/" + idPedido;

        pagoClienteService.solicitarReembolso(idPedido);

        verify(restTemplate).put(url, null);
    }

    @Test
    void solicitarReembolso_siPagosFalla_noLanzaExcepcion() {
        Long idPedido = 1L;

        doThrow(new RuntimeException("MS Pagos caído"))
                .when(restTemplate)
                .put(anyString(), isNull());

        assertDoesNotThrow(() -> pagoClienteService.solicitarReembolso(idPedido));

        verify(restTemplate).put(anyString(), isNull());
    }
}