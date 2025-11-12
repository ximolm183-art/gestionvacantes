package com.xolmos.gestionvacantes.controller;

import com.xolmos.gestionvacantes.dto.ReporteAspiranteDTO;
import com.xolmos.gestionvacantes.dto.ReporteDesempenoDTO;
import com.xolmos.gestionvacantes.dto.ReporteVacanteDTO;
import com.xolmos.gestionvacantes.model.Empleador;
import com.xolmos.gestionvacantes.service.EmpleadorService;
import com.xolmos.gestionvacantes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/empleador/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final EmpleadorService empleadorService;

    // ========== REPORTE 1: Vacantes Publicadas ==========
    @GetMapping("/vacantes")
    public String reporteVacantes(Authentication auth, Model model) {
        String email = auth.getName();
        Empleador empleador = empleadorService.buscarPorCorreo(email);

        List<ReporteVacanteDTO> reporte = reporteService.generarReporteVacantes(empleador.getId());

        // CALCULAR TOTALES AQUÍ
        long totalAspirantes = reporte.stream()
                .mapToLong(ReporteVacanteDTO::getNumeroAspirantes)
                .sum();

        model.addAttribute("reporte", reporte);
        model.addAttribute("empleador", empleador);
        model.addAttribute("totalAspirantes", totalAspirantes);

        return "empleador/reporte-vacantes";
    }

    // ========== REPORTE 2: Aspirantes por Vacante ==========
    @GetMapping("/aspirantes/{vacanteId}")
    public String reporteAspirantes(@PathVariable Long vacanteId, Model model) {
        List<ReporteAspiranteDTO> reporte = reporteService.generarReporteAspirantes(vacanteId);

        model.addAttribute("reporte", reporte);
        model.addAttribute("vacanteId", vacanteId);

        return "empleador/reporte-aspirantes";
    }

    // ========== REPORTE 3: Desempeño de Vacantes ==========
    @GetMapping("/desempeno")
    public String reporteDesempeno(Authentication auth, Model model) {
        String email = auth.getName();
        Empleador empleador = empleadorService.buscarPorCorreo(email);

        List<ReporteDesempenoDTO> reporte = reporteService.generarReporteDesempeno(empleador.getId());

        // CALCULAR TOTALES AQUÍ
        long totalPostulaciones = reporte.stream()
                .mapToLong(ReporteDesempenoDTO::getNumeroPostulaciones)
                .sum();

        long totalContratados = reporte.stream()
                .mapToLong(ReporteDesempenoDTO::getCandidatosAceptados)
                .sum();

        double tasaPromedio = reporte.isEmpty() ? 0.0 :
                reporte.stream()
                        .mapToDouble(ReporteDesempenoDTO::getPorcentajeContratacion)
                        .average()
                        .orElse(0.0);

        model.addAttribute("reporte", reporte);
        model.addAttribute("empleador", empleador);
        model.addAttribute("totalPostulaciones", totalPostulaciones);
        model.addAttribute("totalContratados", totalContratados);
        model.addAttribute("tasaPromedio", tasaPromedio);

        return "empleador/reporte-desempeno";
    }
}
