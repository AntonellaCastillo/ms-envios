package com.perfulandia.ms_envios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// ENTIDAD ENVIO (despacho físico del pedido) — HU-33, HU-34.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "envio")
public class Envio 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEnvio;

    // Id Externo hacia el pedido. Un envío pertenece a un pedido.
    @NotNull(message = "El pedido es obligatorio")
    private Long idPedido;

    // Id Externo: sucursal de origen (MS Sucursales)
    private Long idSucursal;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    // Número de tracking único (HU-33)
    private String tracking;

    @NotNull(message = "El estado del envío es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
}