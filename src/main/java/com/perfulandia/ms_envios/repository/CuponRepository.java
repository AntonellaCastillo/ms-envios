package com.perfulandia.ms_envios.repository;

import com.perfulandia.ms_envios.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// CAPA REPOSITORY de Cupon - hereda el CRUD de JpaRepository
@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> 
{

    // HU-27: buscar un cupón por su código (para validarlo y aplicarlo)
    Optional<Cupon> findByCodigo(String codigo);
}