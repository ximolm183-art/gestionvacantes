package com.xolmos.gestionvacantes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empleadores")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Empleador extends Usuario{


    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Column(nullable = false, length = 150)
    private String empresa;
}
