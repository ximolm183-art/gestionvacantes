package com.xolmos.gestionvacantes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoDTO {
    private Long vacanteId;
    private String titulo;
    private Long numeroPostulaciones;
    private Long candidatosAceptados;
    private Double porcentajeContratacion;
    private Long diasPromedioCierre;
}
