package com.perfulandia.ms_envios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// DTO para reservar/apartar stock en MS Productos.
// Debe calzar con el AjusteStockDTO que Productos espera en /api/inventario/apartar.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaStockDTO {
    private Long idProducto;
    private Long idSucursal;
    private Integer cantidad;
    private String idOperacion; // idempotencia: identifica la operación para no duplicar
}