package com.perfulandia.ms_envios.controller;

import com.perfulandia.ms_envios.model.Cupon;
import com.perfulandia.ms_envios.service.CuponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cupones")
public class CuponController 
{

    @Autowired
    private CuponService cuponService;

    // POST: crear cupón
    @PostMapping
    public ResponseEntity<Cupon> crearCupon(@Valid @RequestBody Cupon cupon) 
    {
        Cupon nuevo = cuponService.guardarCupon(cupon);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED); // 201
    }

    // GET: listar todos
    @GetMapping
    public List<Cupon> obtenerTodos() 
    {
        return cuponService.listarCupones();
    }

    // GET por id
    @GetMapping("/{id}")
    public ResponseEntity<Cupon> obtenerPorId(@PathVariable Long id) 
    {
        return cuponService.obtenerCuponPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET por código (HU-27)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Cupon> obtenerPorCodigo(@PathVariable String codigo) 
    {
        return cuponService.obtenerCuponPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Cupon> actualizar(@PathVariable Long id, @Valid @RequestBody Cupon cupon) 
    {
        return ResponseEntity.ok(cuponService.actualizarCupon(id, cupon));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) 
    {
        cuponService.eliminarCupon(id);
        return ResponseEntity.noContent().build();
    }
}