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
@RequestMapping("/api/mascotas")
@Tag(name = "Mascotas", description = "Pacientes de la veterinaria (CRUD y consulta por cliente)")
public class MascotaControlador extends CrudControlador<Mascota> {

    @Autowired
    private MascotaServicio servicio;

    @Override
    protected ICrudServicio<Mascota> servicio() {
        return servicio;
    }

    @Operation(summary = "Mascotas de un cliente", description = "Lista las mascotas que pertenecen al cliente indicado.")
    @GetMapping("/cliente/{idCliente}")
    public List<Mascota> porCliente(@PathVariable("idCliente") Long idCliente) {
        return servicio.porCliente(idCliente);
    }
}
