package com.perfulandia.ms_envios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoClienteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductoClienteService productoClienteService;

    @BeforeEach
    void setUp() {
        // Como urlProductos viene desde @Value, en test la seteamos manualmente.
        ReflectionTestUtils.setField(
                productoClienteService,
                "urlProductos",
                "http://localhost:8082"
        );
    }

    @Test
    void validarProducto_productoExiste_devuelveTrue() {
        Long idProducto = 1L;
        String url = "http://localhost:8082/api/v1/productos/" + idProducto;

        when(restTemplate.getForEntity(url, Object.class))
                .thenReturn(ResponseEntity.ok(new Object()));

        boolean resultado = productoClienteService.validarProducto(idProducto);

        assertTrue(resultado);
        verify(restTemplate).getForEntity(url, Object.class);
    }

    @Test
    void validarProducto_errorEnProductos_devuelveFalse() {
        Long idProducto = 99L;
        String url = "http://localhost:8082/api/v1/productos/" + idProducto;

        when(restTemplate.getForEntity(url, Object.class))
                .thenThrow(new RuntimeException("MS Productos no disponible"));

        boolean resultado = productoClienteService.validarProducto(idProducto);

        assertFalse(resultado);
        verify(restTemplate).getForEntity(url, Object.class);
    }

    @Test
    void reservarStock_respuestaExitosa_devuelveTrue() {
        Long idProducto = 1L;
        Integer cantidad = 3;
        String url = "http://localhost:8082/api/v1/inventario/reservar";

        when(restTemplate.postForEntity(eq(url), any(Object.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        boolean resultado = productoClienteService.reservarStock(idProducto, cantidad);

        assertTrue(resultado);
        verify(restTemplate).postForEntity(eq(url), any(Object.class), eq(Void.class));
    }

    @Test
    void reservarStock_errorEnProductos_devuelveFalse() {
        Long idProducto = 1L;
        Integer cantidad = 3;
        String url = "http://localhost:8082/api/v1/inventario/reservar";

        when(restTemplate.postForEntity(eq(url), any(Object.class), eq(Void.class)))
                .thenThrow(new RuntimeException("No se pudo reservar stock"));

        boolean resultado = productoClienteService.reservarStock(idProducto, cantidad);

        assertFalse(resultado);
        verify(restTemplate).postForEntity(eq(url), any(Object.class), eq(Void.class));
    }

    @Test
    void cancelarReserva_respuestaExitosa_devuelveTrue() {
        Long idProducto = 1L;
        Integer cantidad = 3;
        String url = "http://localhost:8082/api/v1/inventario/cancelar-reserva";

        when(restTemplate.postForEntity(eq(url), any(Object.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        boolean resultado = productoClienteService.cancelarReserva(idProducto, cantidad);

        assertTrue(resultado);
        verify(restTemplate).postForEntity(eq(url), any(Object.class), eq(Void.class));
    }

    @Test
    void cancelarReserva_errorEnProductos_devuelveFalse() {
        Long idProducto = 1L;
        Integer cantidad = 3;
        String url = "http://localhost:8082/api/v1/inventario/cancelar-reserva";

        when(restTemplate.postForEntity(eq(url), any(Object.class), eq(Void.class)))
                .thenThrow(new RuntimeException("No se pudo cancelar reserva"));

        boolean resultado = productoClienteService.cancelarReserva(idProducto, cantidad);

        assertFalse(resultado);
        verify(restTemplate).postForEntity(eq(url), any(Object.class), eq(Void.class));
    }
}