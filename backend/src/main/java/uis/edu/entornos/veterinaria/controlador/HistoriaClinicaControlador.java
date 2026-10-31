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
@RequestMapping("/api/historias")
@Tag(name = "Historia clínica", description = "Diagnósticos y tratamientos de las mascotas (CRUD y consulta por mascota)")
public class HistoriaClinicaControlador extends CrudControlador<Historiaclinica> {

    @Autowired
    private HistoriaClinicaServicio servicio;

    @Override
    protected ICrudServicio<Historiaclinica> servicio() {
        return servicio;
    }

    @Operation(summary = "Historial de una mascota", description = "Lista la historia clínica de la mascota, de la más reciente a la más antigua.")
    @GetMapping("/mascota/{idMascota}")
    public List<Historiaclinica> porMascota(@PathVariable("idMascota") Long idMascota) {
        return servicio.porMascota(idMascota);
    }
}
