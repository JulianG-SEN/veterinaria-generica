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
@RequestMapping("/api/servicios")
@Tag(name = "Servicios", description = "Servicios que ofrece la veterinaria con precio y duración (CRUD)")
public class ServicioControlador extends CrudControlador<Servicio> {

    @Autowired
    private ServicioClinicaServicio servicio;

    @Override
    protected ICrudServicio<Servicio> servicio() {
        return servicio;
    }
}
