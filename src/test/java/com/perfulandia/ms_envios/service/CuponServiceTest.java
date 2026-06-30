package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.exception.RecursoNoEncontradoException;
import com.perfulandia.ms_envios.model.Cupon;
import com.perfulandia.ms_envios.model.TipoDescuento;
import com.perfulandia.ms_envios.repository.CuponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {

    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private CuponService cuponService;

    @Test
    void guardarCupon_guardaYDevuelve() {
        Cupon c = new Cupon();
        c.setCodigo("VERANO10");

        when(cuponRepository.save(any(Cupon.class))).thenAnswer(i -> i.getArgument(0));

        Cupon resultado = cuponService.guardarCupon(c);

        assertEquals("VERANO10", resultado.getCodigo());
        verify(cuponRepository).save(c);
    }

    @Test
    void listarCupones_devuelveLista() {
        when(cuponRepository.findAll()).thenReturn(Arrays.asList(new Cupon(), new Cupon()));

        assertEquals(2, cuponService.listarCupones().size());

        verify(cuponRepository).findAll();
    }

    @Test
    void obtenerCuponPorId_existe_devuelveCupon() {
        Cupon c = new Cupon();
        c.setIdCupon(1L);

        when(cuponRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<Cupon> resultado = cuponService.obtenerCuponPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCupon());

        verify(cuponRepository).findById(1L);
    }

    @Test
    void obtenerCuponPorId_noExiste_devuelveVacio() {
        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cupon> resultado = cuponService.obtenerCuponPorId(99L);

        assertTrue(resultado.isEmpty());

        verify(cuponRepository).findById(99L);
    }

    @Test
    void obtenerCuponPorCodigo_existe_devuelveCupon() {
        Cupon c = new Cupon();
        c.setCodigo("VERANO10");

        when(cuponRepository.findByCodigo("VERANO10")).thenReturn(Optional.of(c));

        Optional<Cupon> resultado = cuponService.obtenerCuponPorCodigo("VERANO10");

        assertTrue(resultado.isPresent());
        assertEquals("VERANO10", resultado.get().getCodigo());

        verify(cuponRepository).findByCodigo("VERANO10");
    }

    @Test
    void obtenerCuponPorCodigo_noExiste_devuelveVacio() {
        when(cuponRepository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        Optional<Cupon> resultado = cuponService.obtenerCuponPorCodigo("NOEXISTE");

        assertTrue(resultado.isEmpty());

        verify(cuponRepository).findByCodigo("NOEXISTE");
    }

    @Test
    void actualizarCupon_existe_actualizaYDevuelveCupon() {
        Cupon existente = new Cupon();
        existente.setIdCupon(1L);
        existente.setCodigo("ANTIGUO");

        Cupon actualizado = new Cupon();
        actualizado.setCodigo("VERANO20");
        actualizado.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        actualizado.setFechaFin(LocalDateTime.of(2026, 12, 31, 23, 59));
        actualizado.setTipoDescuento(TipoDescuento.PORCENTAJE);
        actualizado.setValorDescuento(new BigDecimal("20"));
        actualizado.setCantidadMaximaUsos(100);
        actualizado.setUsosDisponibles(80);
        actualizado.setActivo(true);

        when(cuponRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(i -> i.getArgument(0));

        Cupon resultado = cuponService.actualizarCupon(1L, actualizado);

        assertEquals("VERANO20", resultado.getCodigo());
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), resultado.getFechaInicio());
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59), resultado.getFechaFin());
        assertEquals(TipoDescuento.PORCENTAJE, resultado.getTipoDescuento());
        assertEquals(0, resultado.getValorDescuento().compareTo(new BigDecimal("20")));
        assertEquals(100, resultado.getCantidadMaximaUsos());
        assertEquals(80, resultado.getUsosDisponibles());
        assertTrue(resultado.getActivo());

        verify(cuponRepository).findById(1L);
        verify(cuponRepository).save(existente);
    }

    @Test
    void actualizarCupon_noExiste_lanzaRecursoNoEncontrado() {
        Cupon actualizado = new Cupon();
        actualizado.setCodigo("VERANO20");

        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> cuponService.actualizarCupon(99L, actualizado));

        verify(cuponRepository).findById(99L);
        verify(cuponRepository, never()).save(any());
    }

    @Test
    void eliminarCupon_llamaDeleteById() {
        cuponService.eliminarCupon(1L);

        verify(cuponRepository).deleteById(1L);
    }
}