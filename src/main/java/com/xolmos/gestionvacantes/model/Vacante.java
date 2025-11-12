package com.xolmos.gestionvacantes.model;
import com.xolmos.gestionvacantes.model.enums.TipoTrabajador;
import com.xolmos.gestionvacantes.model.enums.EstadoVacante;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vacantes")
public class Vacante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe especificar un empleador")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false)
    private Empleador empleador;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String requisitos;

    @Column(length = 100)
    private String ubicacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTrabajador tipoTrabajo;

    @Column(precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    private LocalDateTime fechaCierre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVacante estado = EstadoVacante.BORRADOR;

    // Campo transitorio para las solicitudes
    @Transient
    private List<Solicitud> solicitudes;

    // Getter y Setter (si usas Lombok, ya están incluidos con @Data)
    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }


    @PrePersist
    protected void onCreate() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
    }
}
