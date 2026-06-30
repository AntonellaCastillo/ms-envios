package com.perfulandia.ms_envios.exception;

// EXCEPCIÓN PROPIA: error para cuando una operación no se permite por una regla de negocio.
// Ej HU-48: cancelar un pedido que ya está En Preparación. El handler la convierte en 409.
public class OperacionNoPermitidaException extends RuntimeException 
{
    public OperacionNoPermitidaException(String mensaje) 
    {
        super(mensaje);
    }
}