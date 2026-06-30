package com.perfulandia.ms_envios.controller;

import com.perfulandia.ms_envios.model.Envio;
import com.perfulandia.ms_envios.model.EstadoEnvio;
import com.perfulandia.ms_envios.service.EnvioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {

    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioController envioController;

    @Test
    void crearEnvio_devuelveCreatedYEnvio() {
        Envio envio = crearEnvioEjemplo();
        envio.setIdEnvio(1L);

        when(envioService.guardarEnvio(any(Envio.class))).thenReturn(envio);

        ResponseEntity<Envio> respuesta = envioController.crearEnvio(envio);

        assertEquals(201, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdEnvio());
        assertEquals("TRK-ABC12345", respuesta.getBody().getTracking());

        verify(envioService).guardarEnvio(envio);
    }

    @Test
    void obtenerTodos_devuelveListaDeEnvios() {
        Envio e1 = crearEnvioEjemplo();
        Envio e2 = crearEnvioEjemplo();
        e1.setIdEnvio(1L);
        e2.setIdEnvio(2L);

        when(envioService.listarEnvios()).thenReturn(Arrays.asList(e1, e2));

        List<Envio> resultado = envioController.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(envioService).listarEnvios();
    }

    @Test
    void obtenerPorId_existe_devuelveOk() {
        Envio envio = crearEnvioEjemplo();
        envio.setIdEnvio(1L);

        when(envioService.obtenerEnvioPorId(1L)).thenReturn(Optional.of(envio));

        ResponseEntity<Envio> respuesta = envioController.obtenerPorId(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdEnvio());

        verify(envioService).obtenerEnvioPorId(1L);
    }

    @Test
    void obtenerPorId_noExiste_devuelveNotFound() {
        when(envioService.obtenerEnvioPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Envio> respuesta = envioController.obtenerPorId(99L);

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(envioService).obtenerEnvioPorId(99L);
    }

    @Test
    void rastrear_existe_devuelveOk() {
        Envio envio = crearEnvioEjemplo();

        when(envioService.rastrearEnvio("TRK-ABC12345")).thenReturn(Optional.of(envio));

        ResponseEntity<Envio> respuesta = envioController.rastrear("TRK-ABC12345");

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals("TRK-ABC12345", respuesta.getBody().getTracking());

        verify(envioService).rastrearEnvio("TRK-ABC12345");
    }

    @Test
    void rastrear_noExiste_devuelveNotFound() {
        when(envioService.rastrearEnvio("TRK-NOEXISTE")).thenReturn(Optional.empty());

        ResponseEntity<Envio> respuesta = envioController.rastrear("TRK-NOEXISTE");

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(envioService).rastrearEnvio("TRK-NOEXISTE");
    }

    @Test
    void actualizarEstado_devuelveOkYEnvioActualizado() {
        Envio envio = crearEnvioEjemplo();
        envio.setEstado(EstadoEnvio.EN_RUTA);

        when(envioService.actualizarEstado(1L, EstadoEnvio.EN_RUTA)).thenReturn(envio);

        ResponseEntity<Envio> respuesta = envioController.actualizarEstado(1L, EstadoEnvio.EN_RUTA);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(EstadoEnvio.EN_RUTA, respuesta.getBody().getEstado());

        verify(envioService).actualizarEstado(1L, EstadoEnvio.EN_RUTA);
    }

    @Test
    void eliminar_devuelveNoContent() {
        doNothing().when(envioService).eliminarEnvio(1L);

        ResponseEntity<Void> respuesta = envioController.eliminar(1L);

        assertEquals(204, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(envioService).eliminarEnvio(1L);
    }

    private Envio crearEnvioEjemplo() {
        Envio envio = new Envio();
        envio.setIdPedido(1L);
        envio.setIdSucursal(1L);
        envio.setDireccionDestino("Av. Siempre Viva 742");
        envio.setTracking("TRK-ABC12345");
        envio.setEstado(EstadoEnvio.EN_BODEGA);
        envio.setFechaDespacho(LocalDateTime.of(2026, 6, 30, 10, 0));
        return envio;
    }
}