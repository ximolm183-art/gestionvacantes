package com.xolmos.gestionvacantes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "aspirantes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Aspirante extends Usuario {
    @Column(length = 500)
    private String habilidades;



}
