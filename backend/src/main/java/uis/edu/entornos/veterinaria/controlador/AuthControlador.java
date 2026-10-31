package uis.edu.entornos.veterinaria.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import uis.edu.entornos.veterinaria.modelo.Usuario;
import uis.edu.entornos.veterinaria.servicio.UsuarioServicio;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión de los usuarios del sistema")
public class AuthControlador {

    public record LoginRequest(
            @NotBlank(message = "El nombre de usuario es obligatorio") String nombreUsuario,
            @NotBlank(message = "La contraseña es obligatoria") String password) {
    }

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Operation(summary = "Iniciar sesión", description = "Valida usuario y contraseña (BCrypt) y retorna los datos del usuario con su rol. Responde 401 si son incorrectos.")
    @PostMapping("/login")
    public Usuario login(@Valid @RequestBody LoginRequest peticion) {
        return usuarioServicio.autenticar(peticion.nombreUsuario(), peticion.password());
    }
}
