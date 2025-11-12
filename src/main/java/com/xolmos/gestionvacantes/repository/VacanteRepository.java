package com.xolmos.gestionvacantes.repository;

import com.xolmos.gestionvacantes.model.Empleador;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoVacante;
import com.xolmos.gestionvacantes.model.enums.TipoTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacanteRepository extends JpaRepository<Vacante, Long> {

    // Buscar por estado
    List<Vacante> findByEstado(EstadoVacante estado);

    // Buscar por empleador
    List<Vacante> findByEmpleador(Empleador empleador);

    // Buscar por ubicación (con estado PUBLICADA)
    List<Vacante> findByUbicacionContainingIgnoreCaseAndEstado(String ubicacion, EstadoVacante estado);

    // Buscar por tipo de trabajo (con estado PUBLICADA)
    List<Vacante> findByTipoTrabajoAndEstado(TipoTrabajador tipoTrabajo, EstadoVacante estado);
    // ========== NUEVOS MÉTODOS ==========
    List<Vacante> findByEmpleadorId(Long empleadorId);

    // Buscar por empleador y estado
    List<Vacante> findByEmpleadorAndEstado(Empleador empleador, EstadoVacante estado);

    // Contar vacantes por empleador
    long countByEmpleador(Empleador empleador);

    // Contar vacantes por empleador y estado
    long countByEmpleadorAndEstado(Empleador empleador, EstadoVacante estado);
}
