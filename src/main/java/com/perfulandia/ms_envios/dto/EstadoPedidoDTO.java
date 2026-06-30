package com.perfulandia.ms_envios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// DTO: notifica el cambio de estado de un pedido (HU-25, HU-34).
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPedidoDTO 
{
    private Long idPedido;
    private String estado;
}