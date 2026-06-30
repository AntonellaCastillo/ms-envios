package com.perfulandia.ms_envios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// DTO: datos que MS Productos necesita para reservar stock (HU-23, HU-53).
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaStockDTO 
{
    private Long idProducto;
    private Integer cantidad;
}