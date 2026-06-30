package com.perfulandia.ms_envios.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

// CAPA EXCEPTION - manejo global de errores de TODOS los controllers.
@RestControllerAdvice
public class GlobalExceptionHandler 
{

    // Recurso no encontrado -> 404
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(RecursoNoEncontradoException e) 
    {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    // Operación no permitida por regla de negocio (ej: cancelar pedido ya despachado) -> 409
    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<Map<String, String>> manejarOperacionNoPermitida(OperacionNoPermitidaException e) 
    {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
    }

    // Errores de validación (@Valid) -> 400 con el campo que falló
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException e) 
    {
        Map<String, String> errores = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(campo ->
                errores.put(campo.getField(), campo.getDefaultMessage())
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST); // 400
    }

    // JSON mal formado -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarJsonMalFormado(HttpMessageNotReadableException e) 
    {
        Map<String, String> error = new HashMap<>();
        error.put("error", "El cuerpo de la solicitud no es un JSON válido o no coincide con la estructura esperada");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400
    }

    // Integridad de BD (foreign key, duplicado) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarIntegridadDatos(DataIntegrityViolationException e) 
    {
        Map<String, String> error = new HashMap<>();
        error.put("error", "El recurso ya existe o viola una restricción de la base de datos");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
    }

    // Cualquier otro error -> 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarGeneral(RuntimeException e) 
    {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}