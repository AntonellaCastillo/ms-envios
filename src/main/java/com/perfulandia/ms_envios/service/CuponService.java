package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.model.Cupon;
import com.perfulandia.ms_envios.repository.CuponRepository;
import com.perfulandia.ms_envios.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CuponService {

    @Autowired
    private CuponRepository cuponRepository;

    // Crear un cupón
    public Cupon guardarCupon(Cupon cupon) 
    {
        return cuponRepository.save(cupon);
    }

    // Listar todos los cupones
    public List<Cupon> listarCupones() 
    {
        return cuponRepository.findAll();
    }

    // Buscar un cupón por id
    public Optional<Cupon> obtenerCuponPorId(Long id) 
    {
        return cuponRepository.findById(id);
    }

    // HU-27: buscar un cupón por su código (para validarlo)
    public Optional<Cupon> obtenerCuponPorCodigo(String codigo) 
    {
        return cuponRepository.findByCodigo(codigo);
    }

    // Actualizar un cupón existente
    public Cupon actualizarCupon(Long id, Cupon cupon) 
    {
        Cupon existente = cuponRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El cupón no existe"));
        existente.setCodigo(cupon.getCodigo());
        existente.setFechaInicio(cupon.getFechaInicio());
        existente.setFechaFin(cupon.getFechaFin());
        existente.setTipoDescuento(cupon.getTipoDescuento());
        existente.setValorDescuento(cupon.getValorDescuento());
        existente.setCantidadMaximaUsos(cupon.getCantidadMaximaUsos());
        existente.setUsosDisponibles(cupon.getUsosDisponibles());
        existente.setActivo(cupon.getActivo());
        return cuponRepository.save(existente);
    }

    // Eliminar un cupón
    public void eliminarCupon(Long id) 
    {
        cuponRepository.deleteById(id);
    }
}