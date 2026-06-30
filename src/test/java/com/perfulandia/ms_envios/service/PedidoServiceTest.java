package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.exception.OperacionNoPermitidaException;
import com.perfulandia.ms_envios.exception.RecursoNoEncontradoException;
import com.perfulandia.ms_envios.model.Cupon;
import com.perfulandia.ms_envios.model.DetallePedido;
import com.perfulandia.ms_envios.model.EstadoPedido;
import com.perfulandia.ms_envios.model.Pedido;
import com.perfulandia.ms_envios.model.TipoDescuento;
import com.perfulandia.ms_envios.model.TipoEntrega;
import com.perfulandia.ms_envios.repository.CuponRepository;
import com.perfulandia.ms_envios.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void guardarPedido_conDetalles_calculaSubtotalesTotalYEstadoInicial() {
        Pedido pedido = new Pedido();
        pedido.setTipoEntrega(TipoEntrega.DESPACHO_DOMICILIO);

        DetallePedido detalle1 = new DetallePedido();
        detalle1.setIdProducto(1L);
        detalle1.setCantidad(2);
        detalle1.setPrecioUnitario(new BigDecimal("15000"));

        DetallePedido detalle2 = new DetallePedido();
        detalle2.setIdProducto(2L);
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(new BigDecimal("30000"));

        pedido.setDetalles(Arrays.asList(detalle1, detalle2));

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.guardarPedido(pedido);

        assertEquals(EstadoPedido.PENDIENTE_PAGO, resultado.getEstado());
        assertNotNull(resultado.getFecha());

        assertEquals(0, detalle1.getSubtotal().compareTo(new BigDecimal("30000")));
        assertEquals(0, detalle2.getSubtotal().compareTo(new BigDecimal("30000")));
        assertEquals(0, resultado.getTotal().compareTo(new BigDecimal("60000")));

        assertSame(resultado, detalle1.getPedido());
        assertSame(resultado, detalle2.getPedido());

        verify(pedidoRepository).save(pedido);
    }

    @Test
    void guardarPedido_sinDetalles_totalQuedaCero() {
        Pedido pedido = new Pedido();
        pedido.setTipoEntrega(TipoEntrega.RETIRO_TIENDA);
        pedido.setDetalles(null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.guardarPedido(pedido);

        assertEquals(EstadoPedido.PENDIENTE_PAGO, resultado.getEstado());
        assertEquals(0, resultado.getTotal().compareTo(BigDecimal.ZERO));

        verify(pedidoRepository).save(pedido);
    }

    @Test
    void listarPedidos_devuelveLista() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(new Pedido(), new Pedido()));

        List<Pedido> resultado = pedidoService.listarPedidos();

        assertEquals(2, resultado.size());
        verify(pedidoRepository).findAll();
    }

    @Test
    void obtenerPedidoPorId_existe_devuelvePedido() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdPedido());

        verify(pedidoRepository).findById(1L);
    }

    @Test
    void obtenerPedidoPorId_noExiste_devuelveVacio() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(99L);

        assertTrue(resultado.isEmpty());

        verify(pedidoRepository).findById(99L);
    }

    @Test
    void listarPedidosPorCliente_devuelveHistorial() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(10L);

        when(pedidoRepository.findByIdClienteOrderByFechaDesc(10L))
                .thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.listarPedidosPorCliente(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdCliente());

        verify(pedidoRepository).findByIdClienteOrderByFechaDesc(10L);
    }

    @Test
    void actualizarEstado_existe_cambiaEstado() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.actualizarEstado(1L, EstadoPedido.PAGADO);

        assertEquals(EstadoPedido.PAGADO, resultado.getEstado());

        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void actualizarEstado_noExiste_lanzaRecursoNoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.actualizarEstado(99L, EstadoPedido.PAGADO));

        verify(pedidoRepository).findById(99L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void aplicarCupon_porcentajeValido_aplicaDescuentoYDescuentaUso() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        DetallePedido detalle = new DetallePedido();
        detalle.setSubtotal(new BigDecimal("60000"));

        pedido.setDetalles(List.of(detalle));

        Cupon cupon = new Cupon();
        cupon.setCodigo("VERANO10");
        cupon.setActivo(true);
        cupon.setFechaInicio(LocalDateTime.now().minusDays(1));
        cupon.setFechaFin(LocalDateTime.now().plusDays(1));
        cupon.setUsosDisponibles(100);
        cupon.setTipoDescuento(TipoDescuento.PORCENTAJE);
        cupon.setValorDescuento(new BigDecimal("10"));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(cuponRepository.findByCodigo("VERANO10")).thenReturn(Optional.of(cupon));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.aplicarCupon(1L, "VERANO10");

        assertEquals(0, resultado.getTotal().compareTo(new BigDecimal("54000")));
        assertEquals(cupon, resultado.getCupon());
        assertEquals(99, cupon.getUsosDisponibles());

        verify(pedidoRepository).findById(1L);
        verify(cuponRepository).findByCodigo("VERANO10");
        verify(cuponRepository).save(cupon);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void aplicarCupon_montoFijoValido_aplicaDescuento() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        DetallePedido detalle = new DetallePedido();
        detalle.setSubtotal(new BigDecimal("20000"));

        pedido.setDetalles(List.of(detalle));

        Cupon cupon = new Cupon();
        cupon.setCodigo("DESCUENTO5000");
        cupon.setActivo(true);
        cupon.setFechaInicio(LocalDateTime.now().minusDays(1));
        cupon.setFechaFin(LocalDateTime.now().plusDays(1));
        cupon.setUsosDisponibles(10);
        cupon.setTipoDescuento(TipoDescuento.MONTO_FIJO);
        cupon.setValorDescuento(new BigDecimal("5000"));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(cuponRepository.findByCodigo("DESCUENTO5000")).thenReturn(Optional.of(cupon));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.aplicarCupon(1L, "DESCUENTO5000");

        assertEquals(0, resultado.getTotal().compareTo(new BigDecimal("15000")));
        assertEquals(cupon, resultado.getCupon());
        assertEquals(9, cupon.getUsosDisponibles());

        verify(cuponRepository).save(cupon);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void aplicarCupon_descuentoMayorAlTotal_totalQuedaCero() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        DetallePedido detalle = new DetallePedido();
        detalle.setSubtotal(new BigDecimal("3000"));

        pedido.setDetalles(List.of(detalle));

        Cupon cupon = new Cupon();
        cupon.setCodigo("DESCUENTO5000");
        cupon.setActivo(true);
        cupon.setFechaInicio(LocalDateTime.now().minusDays(1));
        cupon.setFechaFin(LocalDateTime.now().plusDays(1));
        cupon.setUsosDisponibles(5);
        cupon.setTipoDescuento(TipoDescuento.MONTO_FIJO);
        cupon.setValorDescuento(new BigDecimal("5000"));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(cuponRepository.findByCodigo("DESCUENTO5000")).thenReturn(Optional.of(cupon));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.aplicarCupon(1L, "DESCUENTO5000");

        assertEquals(0, resultado.getTotal().compareTo(BigDecimal.ZERO));
        assertEquals(4, cupon.getUsosDisponibles());
    }

    @Test
    void aplicarCupon_pedidoNoExiste_lanzaRecursoNoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.aplicarCupon(99L, "VERANO10"));

        verify(cuponRepository, never()).findByCodigo(anyString());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void aplicarCupon_cuponNoExiste_lanzaRecursoNoEncontrado() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(cuponRepository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.aplicarCupon(1L, "NOEXISTE"));

        verify(pedidoRepository, never()).save(any());
        verify(cuponRepository, never()).save(any());
    }

    @Test
    void aplicarCupon_cuponVencido_lanzaOperacionNoPermitida() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        DetallePedido detalle = new DetallePedido();
        detalle.setSubtotal(new BigDecimal("20000"));
        pedido.setDetalles(List.of(detalle));

        Cupon cupon = new Cupon();
        cupon.setCodigo("VENCIDO");
        cupon.setActivo(true);
        cupon.setFechaInicio(LocalDateTime.now().minusDays(10));
        cupon.setFechaFin(LocalDateTime.now().minusDays(1));
        cupon.setUsosDisponibles(10);
        cupon.setTipoDescuento(TipoDescuento.PORCENTAJE);
        cupon.setValorDescuento(new BigDecimal("10"));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(cuponRepository.findByCodigo("VENCIDO")).thenReturn(Optional.of(cupon));

        assertThrows(OperacionNoPermitidaException.class,
                () -> pedidoService.aplicarCupon(1L, "VENCIDO"));

        verify(pedidoRepository, never()).save(any());
        verify(cuponRepository, never()).save(any());
    }

    @Test
    void aplicarCupon_sinUsosDisponibles_lanzaOperacionNoPermitida() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        DetallePedido detalle = new DetallePedido();
        detalle.setSubtotal(new BigDecimal("20000"));
        pedido.setDetalles(List.of(detalle));

        Cupon cupon = new Cupon();
        cupon.setCodigo("SINUSOS");
        cupon.setActivo(true);
        cupon.setFechaInicio(LocalDateTime.now().minusDays(1));
        cupon.setFechaFin(LocalDateTime.now().plusDays(1));
        cupon.setUsosDisponibles(0);
        cupon.setTipoDescuento(TipoDescuento.PORCENTAJE);
        cupon.setValorDescuento(new BigDecimal("10"));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(cuponRepository.findByCodigo("SINUSOS")).thenReturn(Optional.of(cupon));

        assertThrows(OperacionNoPermitidaException.class,
                () -> pedidoService.aplicarCupon(1L, "SINUSOS"));

        verify(pedidoRepository, never()).save(any());
        verify(cuponRepository, never()).save(any());
    }

    @Test
    void cancelarPedido_estadoPendientePago_dejaCancelado() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.cancelarPedido(1L);

        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());

        verify(pedidoRepository).save(pedido);
    }

    @Test
    void cancelarPedido_estadoPagado_dejaCancelado() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setEstado(EstadoPedido.PAGADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.cancelarPedido(1L);

        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());

        verify(pedidoRepository).save(pedido);
    }

    @Test
    void cancelarPedido_estadoNoPermitido_lanzaOperacionNoPermitida() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setEstado(EstadoPedido.EN_PREPARACION);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(OperacionNoPermitidaException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void cancelarPedido_noExiste_lanzaRecursoNoEncontrado() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.cancelarPedido(99L));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void eliminarPedido_llamaDeleteById() {
        pedidoService.eliminarPedido(1L);

        verify(pedidoRepository).deleteById(1L);
    }
}