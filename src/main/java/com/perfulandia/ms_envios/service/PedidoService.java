package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.model.*;
import com.perfulandia.ms_envios.repository.PedidoRepository;
import com.perfulandia.ms_envios.repository.CuponRepository;
import com.perfulandia.ms_envios.exception.RecursoNoEncontradoException;
import com.perfulandia.ms_envios.exception.OperacionNoPermitidaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService 
{

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CuponRepository cuponRepository;

    // HU-23 / HU-53: crear el pedido (checkout). Calcula el total y lo deja PENDIENTE_PAGO.
    public Pedido guardarPedido(Pedido pedido) 
    {
        // Fecha y estado inicial
        pedido.setFecha(LocalDateTime.now());
        if (pedido.getEstado() == null) 
        {
            pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);
        }
        // Vincula cada detalle con su pedido (cabecera-detalle) y calcula subtotales
        if (pedido.getDetalles() != null) 
        {
            for (DetallePedido d : pedido.getDetalles()) 
            {
                d.setPedido(pedido);
                // subtotal = precio * cantidad
                d.setSubtotal(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            }
        }
        // Calcula el total sumando los subtotales
        pedido.setTotal(calcularTotal(pedido));
        return pedidoRepository.save(pedido);
    }

    // Suma los subtotales de los detalles para obtener el total del pedido
    private BigDecimal calcularTotal(Pedido pedido) 
    {
        BigDecimal total = BigDecimal.ZERO;
        if (pedido.getDetalles() != null) 
        {
            for (DetallePedido d : pedido.getDetalles()) 
            {
                total = total.add(d.getSubtotal());
            }
        }
        return total;
    }

    // Listar todos los pedidos
    public List<Pedido> listarPedidos() 
    {
        return pedidoRepository.findAll();
    }

    // Buscar un pedido por id (HU-25: consultar estado)
    public Optional<Pedido> obtenerPedidoPorId(Long id) 
    {
        return pedidoRepository.findById(id);
    }

    // HU-24: historial de pedidos de un cliente, ordenados por fecha descendente
    public List<Pedido> listarPedidosPorCliente(Long idCliente) 
    {
        return pedidoRepository.findByIdClienteOrderByFechaDesc(idCliente);
    }

    // HU-25 / HU-34: actualizar el estado de un pedido (lo cambian eventos o logística)
    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) 
    {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pedido no existe"));
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    // HU-27: aplicar un cupón. Valida vigencia/usos y recalcula el total con descuento.
    public Pedido aplicarCupon(Long idPedido, String codigoCupon) 
    {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pedido no existe"));

        Cupon cupon = cuponRepository.findByCodigo(codigoCupon)
                .orElseThrow(() -> new RecursoNoEncontradoException("El cupón no existe"));

        // Validar que el cupón esté activo, vigente y con usos disponibles (HU-27)
        LocalDateTime ahora = LocalDateTime.now();
        boolean vigente = cupon.getActivo() != null && cupon.getActivo()
                && cupon.getFechaInicio() != null && cupon.getFechaFin() != null
                && !ahora.isBefore(cupon.getFechaInicio())
                && !ahora.isAfter(cupon.getFechaFin())
                && cupon.getUsosDisponibles() != null && cupon.getUsosDisponibles() > 0;

        if (!vigente)
        {
            throw new OperacionNoPermitidaException("El cupón está expirado o no es válido");
        }

        // Recalcula el total base y aplica el descuento según el tipo
        BigDecimal totalBase = calcularTotal(pedido);
        BigDecimal totalConDescuento;
        if (cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE) 
        {
            // descuento = total * (valor / 100)
            BigDecimal factor = cupon.getValorDescuento().divide(BigDecimal.valueOf(100));
            totalConDescuento = totalBase.subtract(totalBase.multiply(factor));
        } else {
            // MONTO_FIJO: resta el valor directo
            totalConDescuento = totalBase.subtract(cupon.getValorDescuento());
        }
        // El total nunca baja de 0
        if (totalConDescuento.compareTo(BigDecimal.ZERO) < 0) 
        {
            totalConDescuento = BigDecimal.ZERO;
        }

        pedido.setCupon(cupon);
        pedido.setTotal(totalConDescuento);
        // Descuenta un uso del cupón
        cupon.setUsosDisponibles(cupon.getUsosDisponibles() - 1);
        cuponRepository.save(cupon);

        return pedidoRepository.save(pedido);
    }

    // HU-48: cancelar un pedido. Solo si está PENDIENTE_PAGO o PAGADO; si no, 409.
    public Pedido cancelarPedido(Long id) 
    {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pedido no existe"));

        // Regla de negocio: no se puede cancelar si ya está En Preparación o posterior
        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO
                && pedido.getEstado() != EstadoPedido.PAGADO) 
        {
            throw new OperacionNoPermitidaException(
                    "No se puede cancelar: el pedido ya está en preparación o despachado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    // Eliminar un pedido
    public void eliminarPedido(Long id) 
    {
        pedidoRepository.deleteById(id);
    }
}