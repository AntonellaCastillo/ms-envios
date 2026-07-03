package com.perfulandia.ms_envios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Pruebas del ProductoClienteService (comunicación REST resiliente con MS Productos).
@ExtendWith(MockitoExtension.class)
class ProductoClienteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductoClienteService productoClienteService;

    // validarProducto: existe -> true
    @Test
    void validarProducto_existe_devuelveTrue() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean resultado = productoClienteService.validarProducto(1L);

        assertTrue(resultado);
    }

    // validarProducto: si Productos falla -> false (resiliencia)
    @Test
    void validarProducto_error_devuelveFalse() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenThrow(new RuntimeException("Productos caido"));

        boolean resultado = productoClienteService.validarProducto(1L);

        assertFalse(resultado);
    }

    // reservarStock: llama al PUT y devuelve true
    @Test
    void reservarStock_ok_devuelveTrue() {
        doNothing().when(restTemplate).put(anyString(), any());

        boolean resultado = productoClienteService.reservarStock(1L, 1L, 2, "OP-1");

        assertTrue(resultado);
        verify(restTemplate).put(anyString(), any());
    }

    // reservarStock: si Productos falla -> false (resiliencia)
    @Test
    void reservarStock_error_devuelveFalse() {
        doThrow(new RuntimeException("Productos caido")).when(restTemplate).put(anyString(), any());

        boolean resultado = productoClienteService.reservarStock(1L, 1L, 2, "OP-1");

        assertFalse(resultado);
    }

    // cancelarReserva: llama al PUT y devuelve true
    @Test
    void cancelarReserva_ok_devuelveTrue() {
        doNothing().when(restTemplate).put(anyString(), any());

        boolean resultado = productoClienteService.cancelarReserva(1L, 1L, 2, "OP-1");

        assertTrue(resultado);
        verify(restTemplate).put(anyString(), any());
    }

    // cancelarReserva: si Productos falla -> false (resiliencia)
    @Test
    void cancelarReserva_error_devuelveFalse() {
        doThrow(new RuntimeException("Productos caido")).when(restTemplate).put(anyString(), any());

        boolean resultado = productoClienteService.cancelarReserva(1L, 1L, 2, "OP-1");

        assertFalse(resultado);
    }
}