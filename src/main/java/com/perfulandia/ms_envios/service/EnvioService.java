package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.model.Envio;
import com.perfulandia.ms_envios.model.EstadoEnvio;
import com.perfulandia.ms_envios.repository.EnvioRepository;
import com.perfulandia.ms_envios.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    // HU-33: crear un envío. Genera un tracking único y lo deja EN_BODEGA.
    public Envio guardarEnvio(Envio envio) 
    {
        // Genera un número de tracking único (HU-33)
        if (envio.getTracking() == null || envio.getTracking().isBlank()) 
        {
            envio.setTracking("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        // Si no trae estado, empieza en bodega
        if (envio.getEstado() == null) 
        {
            envio.setEstado(EstadoEnvio.EN_BODEGA);
        }
        envio.setFechaDespacho(LocalDateTime.now());
        return envioRepository.save(envio);
    }

    // Listar todos los envíos
    public List<Envio> listarEnvios() 
    {
        return envioRepository.findAll();
    }

    // Buscar un envío por id
    public Optional<Envio> obtenerEnvioPorId(Long id) 
    {
        return envioRepository.findById(id);
    }

    // HU-34: rastrear un envío por su tracking
    public Optional<Envio> rastrearEnvio(String tracking) 
    {
        return envioRepository.findByTracking(tracking);
    }

    // HU-34: actualizar el estado del envío (EN_BODEGA -> EN_RUTA -> ENTREGADO)
    public Envio actualizarEstado(Long id, EstadoEnvio nuevoEstado) 
    {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El envío no existe"));
        envio.setEstado(nuevoEstado);
        // Al marcar ENTREGADO, registra la fecha de entrega
        if (nuevoEstado == EstadoEnvio.ENTREGADO) 
        {
            envio.setFechaEntrega(LocalDateTime.now());
        }
        return envioRepository.save(envio);
    }

    // Eliminar un envío
    public void eliminarEnvio(Long id) 
    {
        envioRepository.deleteById(id);
    }
}