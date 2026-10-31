package uis.edu.entornos.veterinaria.controlador;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.servicio.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Usuarios del sistema (administrador, veterinario, recepcionista). La contraseña se cifra con BCrypt y nunca se devuelve")
public class UsuarioControlador extends CrudControlador<Usuario> {

    @Autowired
    private UsuarioServicio servicio;

    @Override
    protected ICrudServicio<Usuario> servicio() {
        return servicio;
    }

    @Operation(summary = "Veterinarios activos", description = "Lista los usuarios activos con rol VETERINARIO (para agendar citas).")
    @GetMapping("/veterinarios")
    public List<Usuario> veterinarios() {
        return servicio.veterinarios();
    }
}
