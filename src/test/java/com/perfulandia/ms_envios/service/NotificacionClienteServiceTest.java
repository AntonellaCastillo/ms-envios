package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.EstadoPedidoDTO;
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
class NotificacionClienteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificacionClienteService notificacionClienteService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                notificacionClienteService,
                "urlNotificaciones",
                "http://localhost:8090"
        );
    }

    @Test
    void notificarCambioEstado_llamaRestTemplatePostForObject() {
        Long idPedido = 1L;
        String estado = "PAGADO";
        String url = "http://localhost:8090/api/v1/notificaciones";

        when(restTemplate.postForObject(
                eq(url),
                any(EstadoPedidoDTO.class),
                eq(Void.class)
        )).thenReturn(null);

        notificacionClienteService.notificarCambioEstado(idPedido, estado);

        verify(restTemplate).postForObject(
                eq(url),
                any(EstadoPedidoDTO.class),
                eq(Void.class)
        );
    }

    @Test
    void notificarCambioEstado_siNotificacionesFalla_noLanzaExcepcion() {
        doThrow(new RuntimeException("MS Notificaciones caído"))
                .when(restTemplate)
                .postForObject(anyString(), any(EstadoPedidoDTO.class), eq(Void.class));

        assertDoesNotThrow(() ->
                notificacionClienteService.notificarCambioEstado(1L, "EN_RUTA")
        );

        verify(restTemplate).postForObject(
                anyString(),
                any(EstadoPedidoDTO.class),
                eq(Void.class)
        );
    }
}