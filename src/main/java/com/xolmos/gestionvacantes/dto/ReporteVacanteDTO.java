package com.xolmos.gestionvacantes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVacanteDTO {
    private Long vacanteId;
    private String titulo;
    private LocalDateTime fechaPublicacion;
    private Long numeroAspirantes;
    private String estado; // PUBLICADA, CERRADA, BORRADOR
}
