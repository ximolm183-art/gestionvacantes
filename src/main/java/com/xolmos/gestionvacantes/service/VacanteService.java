package com.xolmos.gestionvacantes.service;

import com.xolmos.gestionvacantes.model.Aspirante;
import com.xolmos.gestionvacantes.model.Empleador;
import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoVacante;
import com.xolmos.gestionvacantes.model.enums.TipoTrabajador;
import com.xolmos.gestionvacantes.repository.VacanteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacanteService {

    @Autowired
    private VacanteRepository vacanteRepository;

    /**
     * Obtener solo vacantes PUBLICADAS (para aspirantes)
     */
    public List<Vacante> obtenerVacantesPublicadas() {
        return vacanteRepository.findByEstado(EstadoVacante.PUBLICADA);
    }

    /**
     * Obtener TODAS las vacantes de un empleador (incluyendo borradores)
     */
    public List<Vacante> obtenerVacantesPorEmpleador(Empleador empleador) {
        return vacanteRepository.findByEmpleador(empleador);
    }

    public Vacante obtenerPorId(Long id) {
        return vacanteRepository.findById(id).orElse(null);
    }

    public List<Vacante> buscarPorUbicacion(String ubicacion) {
        return vacanteRepository.findByUbicacionContainingIgnoreCaseAndEstado(ubicacion, EstadoVacante.PUBLICADA);
    }

    public List<Vacante> buscarPorTipoTrabajo(TipoTrabajador tipoTrabajo) {
        return vacanteRepository.findByTipoTrabajoAndEstado(tipoTrabajo, EstadoVacante.PUBLICADA);
    }

    public Vacante crearVacante(Vacante vacante) {
        vacante.setFechaPublicacion(LocalDateTime.now());
        return vacanteRepository.save(vacante);
    }

    /**
     * ✅ ACTUALIZAR VACANTE (SIN ENVÍO DE CORREOS)
     */
    @Transactional
    public Vacante actualizarVacante(Vacante vacante) {
        return vacanteRepository.save(vacante);
    }

    public void eliminarVacante(Long id) {
        vacanteRepository.deleteById(id);
    }
}
