package com.perfulandia.ms_envios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ENTIDAD PEDIDO (cabecera de la compra web).
// Cubre cliente registrado e invitado (HU-22, 23, 24, 25, 27, 48, 53, 54).
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    // Id Externo: el cliente vive en MS Usuarios. Es opcional (invitado = null).
    private Long idCliente;

    // Datos del invitado (HU-53): solo se llenan si NO hay idCliente
    private String nombreInvitado;
    private String correoInvitado;
    private String direccionInvitado;

    private LocalDateTime fecha;

    // Estado del pedido (enum). Empieza en PENDIENTE_PAGO.
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    // Despacho a domicilio o retiro en tienda (HU-54)
    @NotNull(message = "El tipo de entrega es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;

    // Id Externo: si es retiro, en qué sucursal (MS Sucursales). Opcional.
    private Long idSucursalRetiro;

    // FK real: el cupón aplicado (vive en MI MS). Opcional.
    @ManyToOne
    @JoinColumn(name = "id_cupon")
    private Cupon cupon;

    private BigDecimal total;

    // Relación cabecera-detalle: un pedido tiene muchas líneas
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;
}
