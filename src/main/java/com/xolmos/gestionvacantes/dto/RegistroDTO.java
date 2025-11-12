package com.xolmos.gestionvacantes.dto;

import com.xolmos.gestionvacantes.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegistroDTO {


    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirmarContrasena;

    @NotNull(message = "Debe seleccionar un tipo de usuario")
    private Role rol;

    // Campos específicos para Aspirante
    private String habilidades;

    // Campos específicos para Empleador
    private String empresa;

    // Constructor vacío (Lombok lo genera con @Data, pero lo ponemos explícito por si acaso)
    public RegistroDTO() {
    }
}
