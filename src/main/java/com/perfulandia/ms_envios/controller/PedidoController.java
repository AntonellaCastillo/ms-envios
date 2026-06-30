package com.perfulandia.ms_envios.controller;

import com.perfulandia.ms_envios.model.Pedido;
import com.perfulandia.ms_envios.model.EstadoPedido;
import com.perfulandia.ms_envios.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController 
{

    @Autowired
    private PedidoService pedidoService;

    // POST: crear pedido / checkout (HU-23, HU-53) -> 201
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody Pedido pedido) 
    {
        Pedido nuevo = pedidoService.guardarPedido(pedido);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED); // 201
    }

    // GET: listar todos
    @GetMapping
    public List<Pedido> obtenerTodos() 
    {
        return pedidoService.listarPedidos();
    }

    // GET por id (HU-25: consultar estado)
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) 
    {
        return pedidoService.obtenerPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET historial por cliente (HU-24)
    @GetMapping("/cliente/{idCliente}")
    public List<Pedido> obtenerPorCliente(@PathVariable Long idCliente) 
    {
        return pedidoService.listarPedidosPorCliente(idCliente);
    }

    // PUT estado (HU-25/HU-34)
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id,
                                                   @RequestParam EstadoPedido nuevoEstado) 
    {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, nuevoEstado));
    }

    // PUT aplicar cupón (HU-27)
    @PutMapping("/{id}/cupon")
    public ResponseEntity<Pedido> aplicarCupon(@PathVariable Long id,
                                               @RequestParam String codigoCupon) 
    {
        return ResponseEntity.ok(pedidoService.aplicarCupon(id, codigoCupon));
    }

    // PUT cancelar (HU-48) -> 409 si no se puede (lo maneja el handler)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}