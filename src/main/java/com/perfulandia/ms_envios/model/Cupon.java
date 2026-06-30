package com.perfulandia.ms_envios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ENTIDAD CUPON (HU-27): código promocional con descuento.
// Vive en MI microservicio, por eso Pedido lo referencia como FK real.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cupon")
public class Cupon 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCupon;

    @NotBlank(message = "El código del cupón es obligatorio")
    private String codigo;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Si es PORCENTAJE o MONTO_FIJO
    @NotNull(message = "El tipo de descuento es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoDescuento tipoDescuento;

    // El valor: 10 (% ) o 5000 (pesos), según el tipo
    @NotNull(message = "El valor del descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal valorDescuento;

    private Integer cantidadMaximaUsos;
    private Integer usosDisponibles;

    private Boolean activo;
}