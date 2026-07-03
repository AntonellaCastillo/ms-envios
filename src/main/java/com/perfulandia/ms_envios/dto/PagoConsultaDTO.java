package com.perfulandia.ms_envios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// DTO para recibir el pago que MS Pagos devuelve al consultarlo por pedido.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoConsultaDTO {
    private Long idPago;
    private Long idPedido;
    private String estado;
}