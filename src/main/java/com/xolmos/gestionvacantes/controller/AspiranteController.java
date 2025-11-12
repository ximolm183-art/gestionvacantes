package com.xolmos.gestionvacantes.controller;

import com.xolmos.gestionvacantes.model.Aspirante;
import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoSolicitud;
import com.xolmos.gestionvacantes.model.enums.TipoTrabajador;
import com.xolmos.gestionvacantes.repository.AspiranteRepository;
import com.xolmos.gestionvacantes.service.SolicitudService;
import com.xolmos.gestionvacantes.service.VacanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/aspirante")
public class AspiranteController {

    @Autowired
    private VacanteService vacanteService;

    @Autowired
    private AspiranteRepository aspiranteRepository;

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("correo", authentication.getName());
        return "aspirante/dashboard";
    }

    @GetMapping("/vacantes")
    public String buscarVacantes(
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) TipoTrabajador tipoTrabajo,
            @RequestParam(required = false, defaultValue = "false") Boolean mostrarTodas,
            Authentication authentication,
            Model model) {

        List<Vacante> vacantes;

        // Filtrar según los parámetros recibidos
        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            vacantes = vacanteService.buscarPorUbicacion(ubicacion);
        } else if (tipoTrabajo != null) {
            vacantes = vacanteService.buscarPorTipoTrabajo(tipoTrabajo);
        } else {
            vacantes = vacanteService.obtenerVacantesPublicadas();
        }

        // Obtener aspirante actual
        String correo = authentication.getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);

        // Obtener IDs de vacantes a las que ya aplicó
        Set<Long> vacantesAplicadas;
        if (aspirante != null) {
            List<Solicitud> solicitudesAspirante = solicitudService.obtenerSolicitudesPorAspirante(aspirante);
            vacantesAplicadas = solicitudesAspirante.stream()
                    .map(s -> s.getVacante().getId())
                    .collect(Collectors.toSet());

            // Si no quiere ver todas, filtrar las que ya aplicó
            if (!mostrarTodas) {
                vacantes = vacantes.stream()
                        .filter(v -> !vacantesAplicadas.contains(v.getId()))
                        .collect(Collectors.toList());
            }
        } else {
            vacantesAplicadas = new HashSet<>();
        }

        model.addAttribute("vacantes", vacantes);
        model.addAttribute("vacantesAplicadas", vacantesAplicadas);
        model.addAttribute("tiposTrabajo", TipoTrabajador.values());
        model.addAttribute("ubicacionBuscada", ubicacion);
        model.addAttribute("tipoTrabajoBuscado", tipoTrabajo);
        model.addAttribute("mostrarTodas", mostrarTodas);

        return "aspirante/vacantes";
    }

    @GetMapping("/vacantes/{id}")
    public String verDetalleVacante(@PathVariable Long id, Model model, Authentication authentication) {
        Vacante vacante = vacanteService.obtenerPorId(id);

        if (vacante == null) {
            return "redirect:/aspirante/vacantes?error=not-found";
        }

        String correo = authentication.getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);

        boolean yaAplico = false;
        if (aspirante != null) {
            yaAplico = solicitudService.yaAplico(aspirante, vacante);
        }

        model.addAttribute("vacante", vacante);
        model.addAttribute("aspirante", aspirante);
        model.addAttribute("yaAplico", yaAplico);

        return "aspirante/detalle-vacante";
    }

    @GetMapping("/aplicar/{id}")
    public String mostrarFormularioAplicar(@PathVariable Long id, Model model,
                                           Authentication authentication,
                                           RedirectAttributes redirectAttributes) {
        Vacante vacante = vacanteService.obtenerPorId(id);

        if (vacante == null) {
            redirectAttributes.addFlashAttribute("error", "Vacante no encontrada");
            return "redirect:/aspirante/vacantes";
        }

        String correo = authentication.getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);

        if (aspirante != null && solicitudService.yaAplico(aspirante, vacante)) {
            redirectAttributes.addFlashAttribute("error", "Ya has aplicado a esta vacante");
            return "redirect:/aspirante/vacantes/" + id;
        }

        model.addAttribute("vacante", vacante);
        model.addAttribute("aspirante", aspirante);

        return "aspirante/aplicar-vacante";
    }

    @PostMapping("/aplicar/{vacanteId}")
    public String aplicarVacante(@PathVariable Long vacanteId,
                                 @RequestParam(required = false) String mensaje,
                                 @RequestParam("cvFile") MultipartFile cvFile,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Obtener aspirante y vacante
            String correo = authentication.getName();
            Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);
            Vacante vacante = vacanteService.obtenerPorId(vacanteId);

            // Validaciones
            if (aspirante == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión como aspirante");
                return "redirect:/login";
            }

            if (vacante == null) {
                redirectAttributes.addFlashAttribute("error", "Vacante no encontrada");
                return "redirect:/aspirante/vacantes";
            }

            // Verificar si ya aplicó
            if (solicitudService.yaAplico(aspirante, vacante)) {
                redirectAttributes.addFlashAttribute("error", "Ya has aplicado a esta vacante");
                return "redirect:/aspirante/vacantes/" + vacanteId;
            }

            // Validar archivo CV
            if (cvFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes adjuntar tu CV en formato PDF");
                return "redirect:/aspirante/aplicar/" + vacanteId;
            }

            // Validar que sea PDF
            String contentType = cvFile.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                redirectAttributes.addFlashAttribute("error", "El CV debe ser un archivo PDF");
                return "redirect:/aspirante/aplicar/" + vacanteId;
            }

            // ✅ GUARDAR ARCHIVO CV
            String uploadDir = "uploads/cv/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + cvFile.getOriginalFilename();
            String filePath = uploadDir + fileName;

            Path path = Paths.get(filePath);
            Files.copy(cvFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // ✅ CREAR OBJETO SOLICITUD
            Solicitud solicitud = new Solicitud();
            solicitud.setAspirante(aspirante);
            solicitud.setVacante(vacante);
            solicitud.setMensaje(mensaje);
            solicitud.setCvAdjunto(filePath);
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);
            solicitud.setFechaSolicitud(LocalDateTime.now());

            // ✅ GUARDAR LA SOLICITUD
            solicitudService.crearSolicitud(solicitud);

            redirectAttributes.addFlashAttribute("success",
                    "¡Solicitud enviada exitosamente! El empleador revisará tu CV.");
            return "redirect:/aspirante/solicitudes";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al enviar solicitud: " + e.getMessage());
            return "redirect:/aspirante/aplicar/" + vacanteId;
        }
    }
    @GetMapping("/solicitudes")
    public String misSolicitudes(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);

        if (aspirante != null) {
            List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorAspirante(aspirante);
            model.addAttribute("solicitudes", solicitudes);
        }

        return "aspirante/solicitudes";
    }

    @GetMapping("/notificaciones")
    public String notificaciones(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);
        model.addAttribute("aspirante", aspirante);
        return "aspirante/notificaciones";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo).orElse(null);

        if (aspirante == null) {
            return "redirect:/aspirante/dashboard";
        }

        model.addAttribute("aspirante", aspirante);
        return "aspirante/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam String habilidades,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            String correo = authentication.getName();
            Aspirante aspirante = aspiranteRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Aspirante no encontrado"));

            aspirante.setNombre(nombre);
            aspirante.setHabilidades(habilidades);

            aspiranteRepository.save(aspirante);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            return "redirect:/aspirante/perfil";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/aspirante/perfil";
        }
    }
}
