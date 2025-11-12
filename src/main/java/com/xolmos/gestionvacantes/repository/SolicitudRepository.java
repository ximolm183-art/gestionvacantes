package com.xolmos.gestionvacantes.repository;

import com.xolmos.gestionvacantes.model.Aspirante;
import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Buscar solicitudes por aspirante
    List<Solicitud> findByAspirante(Aspirante aspirante);

    // Buscar solicitudes por vacante
    List<Solicitud> findByVacante(Vacante vacante);

    // Buscar solicitudes por ID del empleador (a través de vacante)
    List<Solicitud> findByVacanteEmpleadorId(Long empleadorId);
    // ========== NUEVOS MÉTODOS ==========
    List<Solicitud> findByVacanteId(Long vacanteId);

    // ✅ ESTE MÉTODO ES EL QUE NECESITAS
    // Verificar si un aspirante ya aplicó a una vacante específica
    boolean existsByAspiranteAndVacante(Aspirante aspirante, Vacante vacante);

    // Contar solicitudes por vacante
    long countByVacante(Vacante vacante);

    // Contar solicitudes por aspirante
    long countByAspirante(Aspirante aspirante);

    // NUEVOS MÉTODOS PARA REPORTES

    // Contar solicitudes por vacante
    @Query("SELECT COUNT(s) FROM Solicitud s WHERE s.vacante.id = :vacanteId")
    Long contarSolicitudesPorVacante(@Param("vacanteId") Long vacanteId);

    // Contar solicitudes aceptadas por vacante
    @Query("SELECT COUNT(s) FROM Solicitud s WHERE s.vacante.id = :vacanteId AND s.estado = :estado")
    Long contarSolicitudesPorEstado(@Param("vacanteId") Long vacanteId, @Param("estado") EstadoSolicitud estado);

    // Obtener todas las solicitudes de un empleador
    @Query("SELECT s FROM Solicitud s WHERE s.vacante.empleador.id = :empleadorId")
    List<Solicitud> findByEmpleadorId(@Param("empleadorId") Long empleadorId);
}
