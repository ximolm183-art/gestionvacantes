package com.xolmos.gestionvacantes.service;

import com.xolmos.gestionvacantes.model.Empleador;
import com.xolmos.gestionvacantes.repository.EmpleadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpleadorService {

    private final EmpleadorRepository empleadorRepository;

    // Métodos existentes...

    // ========== NUEVO MÉTODO ==========
    public Empleador buscarPorCorreo(String correo) {
        return empleadorRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado con correo: " + correo));
    }

}
