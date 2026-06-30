package com.perfulandia.ms_envios.repository;

import com.perfulandia.ms_envios.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// CAPA REPOSITORY de DetallePedido - hereda el CRUD de JpaRepository
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> 
{

    // Lista las líneas de un pedido (busca por el idPedido de la cabecera)
    List<DetallePedido> findByPedidoIdPedido(Long idPedido);
}