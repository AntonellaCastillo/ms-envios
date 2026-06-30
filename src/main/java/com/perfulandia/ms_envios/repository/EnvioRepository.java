package com.perfulandia.ms_envios.repository;

import com.perfulandia.ms_envios.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// CAPA REPOSITORY de Envio - hereda el CRUD de JpaRepository
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> 
{

    // HU-34: rastrear un envío por su número de tracking
    Optional<Envio> findByTracking(String tracking);
}