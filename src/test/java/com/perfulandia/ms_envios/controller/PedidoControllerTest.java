package com.perfulandia.ms_envios.controller;

import com.perfulandia.ms_envios.model.EstadoPedido;
import com.perfulandia.ms_envios.model.Pedido;
import com.perfulandia.ms_envios.model.TipoEntrega;
import com.perfulandia.ms_envios.service.PedidoService;
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
class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @Test
    void crearPedido_devuelveCreatedYPedido() {
        Pedido pedido = crearPedidoEjemplo();
        pedido.setIdPedido(1L);

        when(pedidoService.guardarPedido(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<Pedido> respuesta = pedidoController.crearPedido(pedido);

        assertEquals(201, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdPedido());
        assertEquals(EstadoPedido.PENDIENTE_PAGO, respuesta.getBody().getEstado());

        verify(pedidoService).guardarPedido(pedido);
    }

    @Test
    void obtenerTodos_devuelveListaDePedidos() {
        Pedido p1 = crearPedidoEjemplo();
        Pedido p2 = crearPedidoEjemplo();

        p1.setIdPedido(1L);
        p2.setIdPedido(2L);

        when(pedidoService.listarPedidos()).thenReturn(Arrays.asList(p1, p2));

        List<Pedido> resultado = pedidoController.obtenerTodos();

        assertEquals(2, resultado.size());

        verify(pedidoService).listarPedidos();
    }

    @Test
    void obtenerPorId_existe_devuelveOk() {
        Pedido pedido = crearPedidoEjemplo();
        pedido.setIdPedido(1L);

        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));

        ResponseEntity<Pedido> respuesta = pedidoController.obtenerPorId(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdPedido());

        verify(pedidoService).obtenerPedidoPorId(1L);
    }

    @Test
    void obtenerPorId_noExiste_devuelveNotFound() {
        when(pedidoService.obtenerPedidoPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Pedido> respuesta = pedidoController.obtenerPorId(99L);

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(pedidoService).obtenerPedidoPorId(99L);
    }

    @Test
    void obtenerPorCliente_devuelveHistorialDelCliente() {
        Pedido pedido = crearPedidoEjemplo();
        pedido.setIdCliente(10L);

        when(pedidoService.listarPedidosPorCliente(10L)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoController.obtenerPorCliente(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdCliente());

        verify(pedidoService).listarPedidosPorCliente(10L);
    }

    @Test
    void actualizarEstado_devuelveOkYPedidoActualizado() {
        Pedido pedido = crearPedidoEjemplo();
        pedido.setEstado(EstadoPedido.PAGADO);

        when(pedidoService.actualizarEstado(1L, EstadoPedido.PAGADO)).thenReturn(pedido);

        ResponseEntity<Pedido> respuesta = pedidoController.actualizarEstado(1L, EstadoPedido.PAGADO);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(EstadoPedido.PAGADO, respuesta.getBody().getEstado());

        verify(pedidoService).actualizarEstado(1L, EstadoPedido.PAGADO);
    }

    @Test
    void aplicarCupon_devuelveOkYPedidoConDescuento() {
        Pedido pedido = crearPedidoEjemplo();
        pedido.setTotal(new BigDecimal("54000"));

        when(pedidoService.aplicarCupon(1L, "VERANO10")).thenReturn(pedido);

        ResponseEntity<Pedido> respuesta = pedidoController.aplicarCupon(1L, "VERANO10");

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(0, respuesta.getBody().getTotal().compareTo(new BigDecimal("54000")));

        verify(pedidoService).aplicarCupon(1L, "VERANO10");
    }

    @Test
    void cancelarPedido_devuelveOkYPedidoCancelado() {
        Pedido pedido = crearPedidoEjemplo();
        pedido.setEstado(EstadoPedido.CANCELADO);

        when(pedidoService.cancelarPedido(1L)).thenReturn(pedido);

        // En tu PedidoController el método se llama cancelar(), no cancelarPedido().
        ResponseEntity<Pedido> respuesta = pedidoController.cancelar(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(EstadoPedido.CANCELADO, respuesta.getBody().getEstado());

        verify(pedidoService).cancelarPedido(1L);
    }

    @Test
    void eliminar_devuelveNoContent() {
        doNothing().when(pedidoService).eliminarPedido(1L);

        ResponseEntity<Void> respuesta = pedidoController.eliminar(1L);

        assertEquals(204, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(pedidoService).eliminarPedido(1L);
    }

    private Pedido crearPedidoEjemplo() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(10L);
        pedido.setFecha(LocalDateTime.of(2026, 6, 30, 10, 0));
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);
        pedido.setTipoEntrega(TipoEntrega.DESPACHO_DOMICILIO);
        pedido.setTotal(new BigDecimal("60000"));
        return pedido;
    }
}