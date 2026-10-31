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
@RequestMapping("/api/razas")
@Tag(name = "Razas", description = "Catálogo de razas por especie (CRUD)")
public class RazaControlador extends CrudControlador<Raza> {

    @Autowired
    private RazaServicio servicio;

    @Override
    protected ICrudServicio<Raza> servicio() {
        return servicio;
    }

    @Operation(summary = "Razas de una especie", description = "Lista las razas que pertenecen a la especie indicada.")
    @GetMapping("/especie/{idEspecie}")
    public List<Raza> porEspecie(@PathVariable("idEspecie") Long idEspecie) {
        return servicio.porEspecie(idEspecie);
    }
}
