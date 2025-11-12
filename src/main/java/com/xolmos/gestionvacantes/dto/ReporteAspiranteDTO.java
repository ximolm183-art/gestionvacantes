package com.xolmos.gestionvacantes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAspiranteDTO {
    private Long solicitudId;
    private String nombreAspirante;
    private String correoAspirante;
    private LocalDateTime fechaPostulacion;
    private String estadoProceso; // PENDIENTE, ACEPTADA, RECHAZADA
    private String tituloVacante;
}
