package com.xolmos.gestionvacantes.repository;

import com.xolmos.gestionvacantes.model.Aspirante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AspiranteRepository extends JpaRepository<Aspirante, Long> {
    Optional<Aspirante> findByCorreo(String correo);
}
