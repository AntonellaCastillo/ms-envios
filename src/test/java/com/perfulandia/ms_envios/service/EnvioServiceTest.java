package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.exception.RecursoNoEncontradoException;
import com.perfulandia.ms_envios.model.Envio;
import com.perfulandia.ms_envios.model.EstadoEnvio;
import com.perfulandia.ms_envios.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    @Test
    void guardarEnvio_sinTracking_generaTrackingEstadoBodegaYFechaDespacho() {
        Envio envio = new Envio();
        envio.setIdPedido(1L);
        envio.setIdSucursal(1L);
        envio.setDireccionDestino("Av. Siempre Viva 742");

        when(envioRepository.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

        Envio resultado = envioService.guardarEnvio(envio);

        assertNotNull(resultado.getTracking());
        assertTrue(resultado.getTracking().startsWith("TRK-"));
        assertEquals(EstadoEnvio.EN_BODEGA, resultado.getEstado());
        assertNotNull(resultado.getFechaDespacho());

        verify(envioRepository).save(envio);
    }

    @Test
    void guardarEnvio_conTrackingMantieneTracking() {
        Envio envio = new Envio();
        envio.setIdPedido(1L);
        envio.setIdSucursal(1L);
        envio.setDireccionDestino("Av. Siempre Viva 742");
        envio.setTracking("TRK-MANUAL1");
        envio.setEstado(EstadoEnvio.EN_RUTA);

        when(envioRepository.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

        Envio resultado = envioService.guardarEnvio(envio);

        assertEquals("TRK-MANUAL1", resultado.getTracking());
        assertEquals(EstadoEnvio.EN_RUTA, resultado.getEstado());
        assertNotNull(resultado.getFechaDespacho());

        verify(envioRepository).save(envio);
    }

    @Test
    void listarEnvios_devuelveLista() {
        when(envioRepository.findAll()).thenReturn(Arrays.asList(new Envio(), new Envio()));

        List<Envio> resultado = envioService.listarEnvios();

        assertEquals(2, resultado.size());
        verify(envioRepository).findAll();
    }

    @Test
    void obtenerEnvioPorId_existe_devuelveEnvio() {
        Envio envio = new Envio();
        envio.setIdEnvio(1L);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        Optional<Envio> resultado = envioService.obtenerEnvioPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdEnvio());

        verify(envioRepository).findById(1L);
    }

    @Test
    void obtenerEnvioPorId_noExiste_devuelveVacio() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Envio> resultado = envioService.obtenerEnvioPorId(99L);

        assertTrue(resultado.isEmpty());

        verify(envioRepository).findById(99L);
    }

    @Test
    void rastrearEnvio_existe_devuelveEnvio() {
        Envio envio = new Envio();
        envio.setTracking("TRK-ABC12345");

        when(envioRepository.findByTracking("TRK-ABC12345")).thenReturn(Optional.of(envio));

        Optional<Envio> resultado = envioService.rastrearEnvio("TRK-ABC12345");

        assertTrue(resultado.isPresent());
        assertEquals("TRK-ABC12345", resultado.get().getTracking());

        verify(envioRepository).findByTracking("TRK-ABC12345");
    }

    @Test
    void rastrearEnvio_noExiste_devuelveVacio() {
        when(envioRepository.findByTracking("TRK-NOEXISTE")).thenReturn(Optional.empty());

        Optional<Envio> resultado = envioService.rastrearEnvio("TRK-NOEXISTE");

        assertTrue(resultado.isEmpty());

        verify(envioRepository).findByTracking("TRK-NOEXISTE");
    }

    @Test
    void actualizarEstado_existe_cambiaEstadoAEnRuta() {
        Envio envio = new Envio();
        envio.setIdEnvio(1L);
        envio.setEstado(EstadoEnvio.EN_BODEGA);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

        Envio resultado = envioService.actualizarEstado(1L, EstadoEnvio.EN_RUTA);

        assertEquals(EstadoEnvio.EN_RUTA, resultado.getEstado());
        assertNull(resultado.getFechaEntrega());

        verify(envioRepository).findById(1L);
        verify(envioRepository).save(envio);
    }

    @Test
    void actualizarEstado_entregado_registraFechaEntrega() {
        Envio envio = new Envio();
        envio.setIdEnvio(1L);
        envio.setEstado(EstadoEnvio.EN_RUTA);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

        Envio resultado = envioService.actualizarEstado(1L, EstadoEnvio.ENTREGADO);

        assertEquals(EstadoEnvio.ENTREGADO, resultado.getEstado());
        assertNotNull(resultado.getFechaEntrega());

        verify(envioRepository).findById(1L);
        verify(envioRepository).save(envio);
    }

    @Test
    void actualizarEstado_noExiste_lanzaRecursoNoEncontrado() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> envioService.actualizarEstado(99L, EstadoEnvio.EN_RUTA));

        verify(envioRepository).findById(99L);
        verify(envioRepository, never()).save(any());
    }

    @Test
    void eliminarEnvio_llamaDeleteById() {
        envioService.eliminarEnvio(1L);

        verify(envioRepository).deleteById(1L);
    }
}