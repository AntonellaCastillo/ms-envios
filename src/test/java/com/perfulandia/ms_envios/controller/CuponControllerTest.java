package com.perfulandia.ms_envios.controller;

import com.perfulandia.ms_envios.model.Cupon;
import com.perfulandia.ms_envios.model.TipoDescuento;
import com.perfulandia.ms_envios.service.CuponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponControllerTest {

    @Mock
    private CuponService cuponService;

    @InjectMocks
    private CuponController cuponController;

    @Test
    void crearCupon_devuelveCreatedYCupon() {
        Cupon cupon = crearCuponEjemplo();
        cupon.setIdCupon(1L);

        when(cuponService.guardarCupon(any(Cupon.class))).thenReturn(cupon);

        ResponseEntity<Cupon> respuesta = cuponController.crearCupon(cupon);

        assertEquals(201, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdCupon());
        assertEquals("VERANO10", respuesta.getBody().getCodigo());

        verify(cuponService).guardarCupon(cupon);
    }

    @Test
    void obtenerTodos_devuelveListaDeCupones() {
        Cupon c1 = crearCuponEjemplo();
        Cupon c2 = crearCuponEjemplo();
        c1.setIdCupon(1L);
        c2.setIdCupon(2L);

        when(cuponService.listarCupones()).thenReturn(Arrays.asList(c1, c2));

        List<Cupon> resultado = cuponController.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(cuponService).listarCupones();
    }

    @Test
    void obtenerPorId_existe_devuelveOk() {
        Cupon cupon = crearCuponEjemplo();
        cupon.setIdCupon(1L);

        when(cuponService.obtenerCuponPorId(1L)).thenReturn(Optional.of(cupon));

        ResponseEntity<Cupon> respuesta = cuponController.obtenerPorId(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdCupon());

        verify(cuponService).obtenerCuponPorId(1L);
    }

    @Test
    void obtenerPorId_noExiste_devuelveNotFound() {
        when(cuponService.obtenerCuponPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Cupon> respuesta = cuponController.obtenerPorId(99L);

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(cuponService).obtenerCuponPorId(99L);
    }

    @Test
    void obtenerPorCodigo_existe_devuelveOk() {
        Cupon cupon = crearCuponEjemplo();

        when(cuponService.obtenerCuponPorCodigo("VERANO10")).thenReturn(Optional.of(cupon));

        ResponseEntity<Cupon> respuesta = cuponController.obtenerPorCodigo("VERANO10");

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals("VERANO10", respuesta.getBody().getCodigo());

        verify(cuponService).obtenerCuponPorCodigo("VERANO10");
    }

    @Test
    void obtenerPorCodigo_noExiste_devuelveNotFound() {
        when(cuponService.obtenerCuponPorCodigo("NOEXISTE")).thenReturn(Optional.empty());

        ResponseEntity<Cupon> respuesta = cuponController.obtenerPorCodigo("NOEXISTE");

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(cuponService).obtenerCuponPorCodigo("NOEXISTE");
    }

    @Test
    void actualizar_devuelveOkYCuponActualizado() {
        Cupon cupon = crearCuponEjemplo();
        cupon.setIdCupon(1L);
        cupon.setCodigo("INVIERNO20");

        when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(cupon);

        ResponseEntity<Cupon> respuesta = cuponController.actualizar(1L, cupon);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals("INVIERNO20", respuesta.getBody().getCodigo());

        verify(cuponService).actualizarCupon(1L, cupon);
    }

    @Test
    void eliminar_devuelveNoContent() {
        doNothing().when(cuponService).eliminarCupon(1L);

        ResponseEntity<Void> respuesta = cuponController.eliminar(1L);

        assertEquals(204, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(cuponService).eliminarCupon(1L);
    }

    private Cupon crearCuponEjemplo() {
        Cupon cupon = new Cupon();
        cupon.setCodigo("VERANO10");
        cupon.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        cupon.setFechaFin(LocalDateTime.of(2026, 12, 31, 23, 59));
        cupon.setTipoDescuento(TipoDescuento.PORCENTAJE);
        cupon.setValorDescuento(new BigDecimal("10"));
        cupon.setCantidadMaximaUsos(100);
        cupon.setUsosDisponibles(100);
        cupon.setActivo(true);
        return cupon;
    }
}