package com.xolmos.gestionvacantes.model;

import com.xolmos.gestionvacantes.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aspirante_id", nullable = false)
    private Aspirante aspirante;

    @ManyToOne
    @JoinColumn(name = "vacante_id", nullable = false)
    private Vacante vacante;

    @Column(name = "cv_adjunto")
    private String cvAdjunto;

    // ✅ AGREGAR ESTE CAMPO
    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    // Constructores
    public Solicitud() {
        this.fechaSolicitud = LocalDateTime.now();
    }

    public Solicitud(Aspirante aspirante, Vacante vacante, String cvAdjunto, String mensaje) {
        this.aspirante = aspirante;
        this.vacante = vacante;
        this.cvAdjunto = cvAdjunto;
        this.mensaje = mensaje;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aspirante getAspirante() {
        return aspirante;
    }

    public void setAspirante(Aspirante aspirante) {
        this.aspirante = aspirante;
    }

    public Vacante getVacante() {
        return vacante;
    }

    public void setVacante(Vacante vacante) {
        this.vacante = vacante;
    }

    public String getCvAdjunto() {
        return cvAdjunto;
    }

    public void setCvAdjunto(String cvAdjunto) {
        this.cvAdjunto = cvAdjunto;
    }

    // ✅ GETTER Y SETTER PARA MENSAJE
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    @Override
    public String toString() {
        return "Solicitud{" +
                "id=" + id +
                ", aspirante=" + (aspirante != null ? aspirante.getNombre() : "null") +
                ", vacante=" + (vacante != null ? vacante.getTitulo() : "null") +
                ", estado=" + estado +
                ", fechaSolicitud=" + fechaSolicitud +
                '}';
    }
}
