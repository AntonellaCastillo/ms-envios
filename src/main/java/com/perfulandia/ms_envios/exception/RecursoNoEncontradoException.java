
package com.perfulandia.ms_envios.exception;

// EXCEPCIÓN PROPIA: error claro para cuando algo no existe (pedido, envío, cupón).
// El handler la convierte en un 404.
public class RecursoNoEncontradoException extends RuntimeException 
{
    public RecursoNoEncontradoException(String mensaje) 
    {
        super(mensaje);
    }
}