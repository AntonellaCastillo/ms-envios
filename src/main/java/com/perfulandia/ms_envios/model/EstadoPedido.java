package com.perfulandia.ms_envios.model;

// Estados por los que pasa un pedido (HU-23, 25, 48, 54)
public enum EstadoPedido 
{
    PENDIENTE_PAGO,
    PAGADO,
    EN_PREPARACION,
    LISTO_PARA_RETIRO,
    ENVIADO,
    ENTREGADO,
    RETIRADO,
    CANCELADO
}