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
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Agenda de citas: valida que el veterinario no tenga dos citas a la misma hora (CRUD)")
public class CitaControlador extends CrudControlador<Cita> {

    @Autowired
    private CitaServicio servicio;

    @Override
    protected ICrudServicio<Cita> servicio() {
        return servicio;
    }

    @Operation(summary = "Agenda de un veterinario", description = "Lista las citas de un veterinario en una fecha (formato AAAA-MM-DD), ordenadas por hora.")
    @GetMapping("/agenda")
    public List<Cita> agenda(
            @RequestParam("veterinario") Long idVeterinario,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return servicio.agenda(idVeterinario, fecha);
    }
}
