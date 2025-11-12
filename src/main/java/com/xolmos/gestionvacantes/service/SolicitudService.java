package com.xolmos.gestionvacantes.service;

import com.xolmos.gestionvacantes.model.Aspirante;
import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoSolicitud;
import com.xolmos.gestionvacantes.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private EmailService emailService;

    public Solicitud crearSolicitud(Solicitud solicitud) {
        solicitud.setFechaSolicitud(LocalDateTime.now());
        return solicitudRepository.save(solicitud);
    }

    public List<Solicitud> obtenerSolicitudesPorAspirante(Aspirante aspirante) {
        return solicitudRepository.findByAspirante(aspirante);
    }

    public List<Solicitud> obtenerSolicitudesPorVacante(Vacante vacante) {
        return solicitudRepository.findByVacante(vacante);
    }

    public List<Solicitud> obtenerSolicitudesPorEmpleador(Long empleadorId) {
        return solicitudRepository.findByVacanteEmpleadorId(empleadorId);
    }

    public Solicitud obtenerPorId(Long id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    public boolean yaAplico(Aspirante aspirante, Vacante vacante) {
        return solicitudRepository.existsByAspiranteAndVacante(aspirante, vacante);
    }

    // ========================================
    // ✅ NUEVO: ACEPTAR SOLICITUD Y ENVIAR CORREO
    // ========================================
    public Solicitud aceptarSolicitud(Solicitud solicitud) {
        solicitud.setEstado(EstadoSolicitud.ACEPTADA);
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        // Enviar correo de aceptación
        try {
            String emailAspirante = solicitud.getAspirante().getCorreo();
            String nombreAspirante = solicitud.getAspirante().getNombre();
            String tituloVacante = solicitud.getVacante().getTitulo();
            String empresa = solicitud.getVacante().getEmpleador().getEmpresa();

            boolean enviado = emailService.notificarSolicitudAceptada(
                    emailAspirante,
                    nombreAspirante,
                    tituloVacante,
                    empresa
            );

            if (enviado) {
                System.out.println("✅ Email de ACEPTACIÓN enviado a: " + emailAspirante);
            } else {
                System.out.println("❌ Error al enviar email de aceptación a: " + emailAspirante);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
        }

        return solicitudGuardada;
    }

    // ========================================
    // ✅ NUEVO: RECHAZAR SOLICITUD Y ENVIAR CORREO
    // ========================================
    public Solicitud rechazarSolicitud(Solicitud solicitud) {
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        // Enviar correo de rechazo
        try {
            String emailAspirante = solicitud.getAspirante().getCorreo();
            String nombreAspirante = solicitud.getAspirante().getNombre();
            String tituloVacante = solicitud.getVacante().getTitulo();
            String empresa = solicitud.getVacante().getEmpleador().getEmpresa();

            boolean enviado = emailService.notificarSolicitudRechazada(
                    emailAspirante,
                    nombreAspirante,
                    tituloVacante,
                    empresa
            );

            if (enviado) {
                System.out.println("✅ Email de RECHAZO enviado a: " + emailAspirante);
            } else {
                System.out.println("❌ Error al enviar email de rechazo a: " + emailAspirante);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
        }

        return solicitudGuardada;
    }
}