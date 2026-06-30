package com.perfulandia.ms_envios.controller;

import com.perfulandia.ms_envios.model.Envio;
import com.perfulandia.ms_envios.model.EstadoEnvio;
import com.perfulandia.ms_envios.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController 
{

    @Autowired
    private EnvioService envioService;

    // POST: crear envío (HU-33) - genera tracking
    @PostMapping
    public ResponseEntity<Envio> crearEnvio(@Valid @RequestBody Envio envio) 
    {
        Envio nuevo = envioService.guardarEnvio(envio);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED); // 201
    }

    // GET: listar todos
    @GetMapping
    public List<Envio> obtenerTodos() 
    {
        return envioService.listarEnvios();
    }

    // GET por id
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) 
    {
        return envioService.obtenerEnvioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET por tracking (HU-34)
    @GetMapping("/tracking/{tracking}")
    public ResponseEntity<Envio> rastrear(@PathVariable String tracking) 
    {
        return envioService.rastrearEnvio(tracking)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT estado (HU-34): EN_BODEGA -> EN_RUTA -> ENTREGADO
    @PutMapping("/{id}/estado")
    public ResponseEntity<Envio> actualizarEstado(@PathVariable Long id,
                                                  @RequestParam EstadoEnvio nuevoEstado) 
    {
        return ResponseEntity.ok(envioService.actualizarEstado(id, nuevoEstado));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        envioService.eliminarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}