package com.xolmos.gestionvacantes.controller;

import com.xolmos.gestionvacantes.dto.RegistroDTO;
import com.xolmos.gestionvacantes.model.Usuario;
import com.xolmos.gestionvacantes.model.enums.Role;
import com.xolmos.gestionvacantes.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private RegistroService registroService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroDTO());
        // ✅ CORRECCIÓN: Filtrar solo ASPIRANTE y EMPLEADOR
        List<Role> rolesDisponibles = Arrays.asList(Role.ASPIRANTE, Role.EMPLEADOR);
        model.addAttribute("roles", rolesDisponibles);
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        // Validación de errores del formulario
        if (result.hasErrors()) {
            List<Role> rolesDisponibles = Arrays.asList(Role.ASPIRANTE, Role.EMPLEADOR);
            model.addAttribute("roles", rolesDisponibles);
            return "registro";
        }

        // Validación de empleador
        if (registroDTO.getRol() == Role.EMPLEADOR) {
            if (registroDTO.getEmpresa() == null || registroDTO.getEmpresa().trim().isEmpty()) {
                model.addAttribute("error", "El nombre de la empresa es obligatorio para empleadores");
                List<Role> rolesDisponibles = Arrays.asList(Role.ASPIRANTE, Role.EMPLEADOR);
                model.addAttribute("roles", rolesDisponibles);
                return "registro";
            }
        }

        try {
            Usuario usuario = registroService.registrarUsuario(registroDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Registro exitoso. Por favor inicia sesión con tus credenciales.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            List<Role> rolesDisponibles = Arrays.asList(Role.ASPIRANTE, Role.EMPLEADOR);
            model.addAttribute("roles", rolesDisponibles);
            return "registro";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard-redirect";
    }
}
