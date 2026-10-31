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
@RequestMapping("/api/vacunaciones")
@Tag(name = "Vacunación", description = "Registro de vacunas aplicadas y próxima dosis (CRUD y consulta por mascota)")
public class VacunacionControlador extends CrudControlador<Vacunacion> {

    @Autowired
    private VacunacionServicio servicio;

    @Override
    protected ICrudServicio<Vacunacion> servicio() {
        return servicio;
    }

    @Operation(summary = "Carné de vacunación de una mascota", description = "Lista las vacunas aplicadas a la mascota, de la más reciente a la más antigua.")
    @GetMapping("/mascota/{idMascota}")
    public List<Vacunacion> porMascota(@PathVariable("idMascota") Long idMascota) {
        return servicio.porMascota(idMascota);
    }
}
