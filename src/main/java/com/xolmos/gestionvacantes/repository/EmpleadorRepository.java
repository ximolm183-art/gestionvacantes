package com.xolmos.gestionvacantes.repository;

import com.xolmos.gestionvacantes.model.Empleador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadorRepository extends JpaRepository<Empleador, Long> {
    Optional<Empleador> findByCorreo(String correo);
}
