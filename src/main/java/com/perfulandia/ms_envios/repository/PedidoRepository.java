package com.perfulandia.ms_envios.repository;

import com.perfulandia.ms_envios.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// CAPA REPOSITORY de Pedido - habla con la BD, hereda el CRUD de JpaRepository
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> 
{

    // HU-24: historial de pedidos de un cliente, ordenados por fecha descendente.
    // Spring Data crea el SQL solo a partir del nombre del método.
    List<Pedido> findByIdClienteOrderByFechaDesc(Long idCliente);
}