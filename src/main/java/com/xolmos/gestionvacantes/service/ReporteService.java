package com.xolmos.gestionvacantes.service;

import com.xolmos.gestionvacantes.dto.ReporteAspiranteDTO;
import com.xolmos.gestionvacantes.dto.ReporteDesempenoDTO;
import com.xolmos.gestionvacantes.dto.ReporteVacanteDTO;
import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoSolicitud;
import com.xolmos.gestionvacantes.model.enums.EstadoVacante;
import com.xolmos.gestionvacantes.repository.SolicitudRepository;
import com.xolmos.gestionvacantes.repository.VacanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VacanteRepository vacanteRepository;
    private final SolicitudRepository solicitudRepository;

    // ========== REPORTE 1: Vacantes Publicadas ==========
    public List<ReporteVacanteDTO> generarReporteVacantes(Long empleadorId) {
        List<Vacante> vacantes = vacanteRepository.findByEmpleadorId(empleadorId);

        return vacantes.stream()
                .map(vacante -> {
                    Long numAspirantes = solicitudRepository.contarSolicitudesPorVacante(vacante.getId());

                    return new ReporteVacanteDTO(
                            vacante.getId(),
                            vacante.getTitulo(),
                            vacante.getFechaPublicacion(),
                            numAspirantes,
                            vacante.getEstado().getDisplayName()
                    );
                })
                .collect(Collectors.toList());
    }

    // ========== REPORTE 2: Aspirantes por Vacante ==========
    public List<ReporteAspiranteDTO> generarReporteAspirantes(Long vacanteId) {
        List<Solicitud> solicitudes = solicitudRepository.findByVacanteId(vacanteId);

        return solicitudes.stream()
                .map(solicitud -> new ReporteAspiranteDTO(
                        solicitud.getId(),
                        solicitud.getAspirante().getNombre(),
                        solicitud.getAspirante().getCorreo(),
                        solicitud.getFechaSolicitud(),
                        solicitud.getEstado().getDisplayName(),
                        solicitud.getVacante().getTitulo()
                ))
                .collect(Collectors.toList());
    }

    // ========== REPORTE 3: Desempeño de Vacantes ==========
    public List<ReporteDesempenoDTO> generarReporteDesempeno(Long empleadorId) {
        List<Vacante> vacantes = vacanteRepository.findByEmpleadorId(empleadorId);

        return vacantes.stream()
                .map(vacante -> {
                    Long numPostulaciones = solicitudRepository.contarSolicitudesPorVacante(vacante.getId());
                    Long numAceptados = solicitudRepository.contarSolicitudesPorEstado(
                            vacante.getId(),
                            EstadoSolicitud.ACEPTADA
                    );

                    Double porcentaje = numPostulaciones > 0
                            ? (numAceptados * 100.0) / numPostulaciones
                            : 0.0;

                    // Calcular días desde publicación hasta cierre (si está cerrada)
                    Long diasPromedio = 0L;
                    if (vacante.getEstado() == EstadoVacante.CERRADA && vacante.getFechaPublicacion() != null) {
                        diasPromedio = ChronoUnit.DAYS.between(
                                vacante.getFechaPublicacion().toLocalDate(),
                                java.time.LocalDate.now()
                        );
                    }

                    return new ReporteDesempenoDTO(
                            vacante.getId(),
                            vacante.getTitulo(),
                            numPostulaciones,
                            numAceptados,
                            Math.round(porcentaje * 100.0) / 100.0, // 2 decimales
                            diasPromedio
                    );
                })
                .collect(Collectors.toList());
    }

}
