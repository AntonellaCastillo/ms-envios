package com.perfulandia.ms_envios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Prueba del NotificacionClienteService (comunicación REST resiliente con MS Notificaciones).
@ExtendWith(MockitoExtension.class)
class NotificacionClienteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificacionClienteService notificacionClienteService;

    // Comunicación normal: llama al RestTemplate
    @Test
    void notificarCambioEstado_llamaRestTemplatePostForObject() {
        notificacionClienteService.notificarCambioEstado(1L, "PAGADO");
        verify(restTemplate).postForObject(anyString(), any(), eq(Void.class));
    }

    // RESILIENCIA: si Notificaciones falla, el catch atrapa el error y NO se cae
    @Test
    void notificarCambioEstado_siFallaNoLanzaExcepcion() {
        doThrow(new RuntimeException("MS Notificaciones caído"))
                .when(restTemplate).postForObject(anyString(), any(), eq(Void.class));

        notificacionClienteService.notificarCambioEstado(1L, "PAGADO");

        verify(restTemplate).postForObject(anyString(), any(), eq(Void.class));
    }
}