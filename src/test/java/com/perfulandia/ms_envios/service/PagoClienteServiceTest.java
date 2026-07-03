package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.PagoConsultaDTO;
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

// Prueba del PagoClienteService (comunicación REST resiliente con MS Pagos, reembolso HU-48).
@ExtendWith(MockitoExtension.class)
class PagoClienteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PagoClienteService pagoClienteService;

    // Reembolso normal: busca el pago del pedido y luego cambia su estado a RECHAZADO.
    @Test
    void solicitarReembolso_pagoExiste_cambiaEstado() {
        // Paso 1: el GET devuelve un pago con id
        PagoConsultaDTO pago = new PagoConsultaDTO(10L, 1L, "CONFIRMADO");
        when(restTemplate.getForObject(anyString(), eq(PagoConsultaDTO.class))).thenReturn(pago);

        pagoClienteService.solicitarReembolso(1L);

        // Verifica que se llamó al GET (buscar) y al PUT (cambiar estado)
        verify(restTemplate).getForObject(anyString(), eq(PagoConsultaDTO.class));
        verify(restTemplate).put(anyString(), any());
    }

    // Si el pedido no tiene pago (GET devuelve null), NO intenta cambiar estado.
    @Test
    void solicitarReembolso_sinPago_noCambiaEstado() {
        when(restTemplate.getForObject(anyString(), eq(PagoConsultaDTO.class))).thenReturn(null);

        pagoClienteService.solicitarReembolso(1L);

        // Se llamó al GET, pero NO al PUT (porque no había pago)
        verify(restTemplate).getForObject(anyString(), eq(PagoConsultaDTO.class));
        verify(restTemplate, never()).put(anyString(), any());
    }

    // RESILIENCIA: si Pagos falla, el catch atrapa el error y Envíos NO se cae.
    @Test
    void solicitarReembolso_siFallaNoLanzaExcepcion() {
        when(restTemplate.getForObject(anyString(), eq(PagoConsultaDTO.class)))
                .thenThrow(new RuntimeException("MS Pagos caído"));

        // No debe lanzar excepción (el try/catch la atrapa)
        pagoClienteService.solicitarReembolso(1L);

        verify(restTemplate).getForObject(anyString(), eq(PagoConsultaDTO.class));
    }
}