package com.xolmos.gestionvacantes.controller;

import com.xolmos.gestionvacantes.model.Empleador;
import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.model.Vacante;
import com.xolmos.gestionvacantes.model.enums.EstadoSolicitud;
import com.xolmos.gestionvacantes.model.enums.EstadoVacante;
import com.xolmos.gestionvacantes.model.enums.TipoTrabajador;
import com.xolmos.gestionvacantes.repository.EmpleadorRepository;
import com.xolmos.gestionvacantes.service.SolicitudService;
import com.xolmos.gestionvacantes.service.VacanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/empleador")
public class EmpleadorController {

    @Autowired
    private VacanteService vacanteService;

    @Autowired
    private EmpleadorRepository empleadorRepository;

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String correo = authentication.getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

        if (empleador != null) {
            List<Vacante> vacantes = vacanteService.obtenerVacantesPorEmpleador(empleador);
            long totalVacantes = vacantes.size();
            long vacantesPublicadas = vacantes.stream()
                    .filter(v -> v.getEstado() == EstadoVacante.PUBLICADA)
                    .count();

            model.addAttribute("totalVacantes", totalVacantes);
            model.addAttribute("vacantesPublicadas", vacantesPublicadas);
            model.addAttribute("empleador", empleador);
        }

        model.addAttribute("correo", correo);
        return "empleador/dashboard";
    }

    @GetMapping("/vacantes")
    public String misVacantes(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

        if (empleador != null) {
            List<Vacante> vacantes = vacanteService.obtenerVacantesPorEmpleador(empleador);
            model.addAttribute("vacantes", vacantes);
        }

        return "empleador/vacantes";
    }

    @GetMapping("/vacantes/nueva")
    public String nuevaVacante(Model model) {
        model.addAttribute("vacante", new Vacante());
        model.addAttribute("tiposTrabajo", TipoTrabajador.values());
        model.addAttribute("estadosVacante", EstadoVacante.values());
        return "empleador/nueva-vacante";
    }

    @PostMapping("/vacantes/nueva")
    public String crearVacante(@RequestParam String titulo,
                               @RequestParam String descripcion,
                               @RequestParam(required = false) String requisitos,
                               @RequestParam(required = false) String ubicacion,
                               @RequestParam TipoTrabajador tipoTrabajo,
                               @RequestParam(required = false) BigDecimal salario,
                               @RequestParam EstadoVacante estado,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

            Vacante vacante = new Vacante();
            vacante.setEmpleador(empleador);
            vacante.setTitulo(titulo);
            vacante.setDescripcion(descripcion);
            vacante.setRequisitos(requisitos);
            vacante.setUbicacion(ubicacion);
            vacante.setTipoTrabajo(tipoTrabajo);
            vacante.setSalario(salario);
            vacante.setEstado(estado);

            vacanteService.crearVacante(vacante);

            redirectAttributes.addFlashAttribute("success", "Vacante creada exitosamente");
            return "redirect:/empleador/vacantes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear vacante: " + e.getMessage());
            return "redirect:/empleador/vacantes/nueva";
        }
    }

    @GetMapping("/vacantes/{id}/editar")
    public String editarVacante(@PathVariable Long id, Model model, Authentication authentication) {
        Vacante vacante = vacanteService.obtenerPorId(id);

        if (vacante == null) {
            return "redirect:/empleador/vacantes";
        }

        String correo = authentication.getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

        if (empleador == null || !vacante.getEmpleador().getId().equals(empleador.getId())) {
            return "redirect:/empleador/vacantes";
        }

        model.addAttribute("vacante", vacante);
        model.addAttribute("tiposTrabajo", TipoTrabajador.values());
        model.addAttribute("estadosVacante", EstadoVacante.values());

        return "empleador/editar-vacante";
    }

    @PostMapping("/vacantes/{id}/editar")
    public String actualizarVacante(@PathVariable Long id,
                                    @RequestParam String titulo,
                                    @RequestParam String descripcion,
                                    @RequestParam(required = false) String requisitos,
                                    @RequestParam(required = false) String ubicacion,
                                    @RequestParam TipoTrabajador tipoTrabajo,
                                    @RequestParam(required = false) BigDecimal salario,
                                    @RequestParam EstadoVacante estado,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Vacante vacante = vacanteService.obtenerPorId(id);

            if (vacante == null) {
                redirectAttributes.addFlashAttribute("error", "Vacante no encontrada");
                return "redirect:/empleador/vacantes";
            }

            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

            if (empleador == null || !vacante.getEmpleador().getId().equals(empleador.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta vacante");
                return "redirect:/empleador/vacantes";
            }

            vacante.setTitulo(titulo);
            vacante.setDescripcion(descripcion);
            vacante.setRequisitos(requisitos);
            vacante.setUbicacion(ubicacion);
            vacante.setTipoTrabajo(tipoTrabajo);
            vacante.setSalario(salario);
            vacante.setEstado(estado);

            vacanteService.actualizarVacante(vacante);

            redirectAttributes.addFlashAttribute("success", "Vacante actualizada exitosamente");
            return "redirect:/empleador/vacantes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar vacante: " + e.getMessage());
            return "redirect:/empleador/vacantes/" + id + "/editar";
        }
    }

    @PostMapping("/vacantes/{id}/eliminar")
    public String eliminarVacante(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            Vacante vacante = vacanteService.obtenerPorId(id);

            if (vacante == null) {
                redirectAttributes.addFlashAttribute("error", "Vacante no encontrada");
                return "redirect:/empleador/vacantes";
            }

            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

            if (empleador == null || !vacante.getEmpleador().getId().equals(empleador.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta vacante");
                return "redirect:/empleador/vacantes";
            }

            vacanteService.eliminarVacante(id);

            redirectAttributes.addFlashAttribute("success", "Vacante eliminada exitosamente");
            return "redirect:/empleador/vacantes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar vacante: " + e.getMessage());
            return "redirect:/empleador/vacantes";
        }
    }

    // ========================================
    // ✅ NUEVO: VER TODAS LAS SOLICITUDES
    // ========================================
    @GetMapping("/solicitudes")
    public String solicitudes(@RequestParam(required = false) String filtro,
                              Model model,
                              Authentication authentication) {
        try {
            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

            if (empleador == null) {
                return "redirect:/login";
            }

            // Obtener TODAS las solicitudes del empleador
            List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorEmpleador(empleador.getId());

            // Aplicar filtros
            if (filtro == null || filtro.equals("pendientes")) {
                solicitudes = solicitudes.stream()
                        .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                        .collect(Collectors.toList());
                filtro = "pendientes";
            } else if (filtro.equals("aceptadas")) {
                solicitudes = solicitudes.stream()
                        .filter(s -> s.getEstado() == EstadoSolicitud.ACEPTADA)
                        .collect(Collectors.toList());
            } else if (filtro.equals("rechazadas")) {
                solicitudes = solicitudes.stream()
                        .filter(s -> s.getEstado() == EstadoSolicitud.RECHAZADA)
                        .collect(Collectors.toList());
            }

            model.addAttribute("solicitudes", solicitudes);
            model.addAttribute("filtroActual", filtro);
            model.addAttribute("empleador", empleador);

            return "empleador/solicitudes";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/empleador/dashboard";
        }
    }

    // ========================================
    // ✅ NUEVO: ACEPTAR SOLICITUD
    // ========================================
    @PostMapping("/solicitudes/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 Intentando aceptar solicitud ID: " + id);

            Solicitud solicitud = solicitudService.obtenerPorId(id);

            if (solicitud == null) {
                System.out.println("❌ Solicitud no encontrada");
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada");
                return "redirect:/empleador/solicitudes";
            }

            System.out.println("✅ Solicitud encontrada: " + solicitud.getAspirante().getNombre());

            // Verificar permisos
            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

            if (empleador == null || !solicitud.getVacante().getEmpleador().getId().equals(empleador.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso");
                return "redirect:/empleador/solicitudes";
            }

            // Aceptar
            solicitudService.aceptarSolicitud(solicitud);

            redirectAttributes.addFlashAttribute("success",
                    "✅ Solicitud aceptada y correo enviado a " + solicitud.getAspirante().getNombre());

            return "redirect:/empleador/solicitudes";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/empleador/solicitudes";
        }
    }

    // ========================================
    // ✅ NUEVO: RECHAZAR SOLICITUD
    // ========================================
    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 Intentando rechazar solicitud ID: " + id);

            Solicitud solicitud = solicitudService.obtenerPorId(id);

            if (solicitud == null) {
                System.out.println("❌ Solicitud no encontrada");
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada");
                return "redirect:/empleador/solicitudes";
            }

            System.out.println("✅ Solicitud encontrada: " + solicitud.getAspirante().getNombre());

            // Verificar permisos
            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

            if (empleador == null || !solicitud.getVacante().getEmpleador().getId().equals(empleador.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso");
                return "redirect:/empleador/solicitudes";
            }

            // Rechazar
            solicitudService.rechazarSolicitud(solicitud);

            redirectAttributes.addFlashAttribute("success",
                    "❌ Solicitud rechazada y correo enviado a " + solicitud.getAspirante().getNombre());

            return "redirect:/empleador/solicitudes";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/empleador/solicitudes";
        }
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo).orElse(null);

        if (empleador == null) {
            return "redirect:/empleador/dashboard";
        }

        model.addAttribute("empleador", empleador);
        return "empleador/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam String empresa,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

            empleador.setNombre(nombre);
            empleador.setEmpresa(empresa);

            empleadorRepository.save(empleador);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            return "redirect:/empleador/perfil";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/empleador/perfil";
        }
    }
    @GetMapping("/descargar-cv/{solicitudId}")
    public ResponseEntity<Resource> descargarCV(@PathVariable Long solicitudId) {
        try {
            Solicitud solicitud = solicitudService.obtenerPorId(solicitudId);

            if (solicitud == null) {
                return ResponseEntity.notFound().build();
            }

            String rutaCV = solicitud.getCvAdjunto();
            Path path = Paths.get(rutaCV);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                String filename = path.getFileName().toString();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
