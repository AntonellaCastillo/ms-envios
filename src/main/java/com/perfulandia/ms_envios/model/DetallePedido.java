package com.perfulandia.ms_envios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

// ENTIDAD DETALLE PEDIDO (el detalle de la compra).
// Cada fila es un producto dentro del pedido. Patrón cabecera-detalle.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetallePedido;

    // FK real hacia el pedido (cabecera). @JsonIgnore evita bucle infinito al serializar.
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonIgnore
    private Pedido pedido;

    // Id Externo: el producto vive en MS Productos
    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
}