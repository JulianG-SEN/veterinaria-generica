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
@RequestMapping("/api/tipodocumentos")
@Tag(name = "Tipos de documento", description = "Catálogo de tipos de documento (CRUD)")
public class TipoDocumentoControlador extends CrudControlador<Tipodocumento> {

    @Autowired
    private TipoDocumentoServicio servicio;

    @Override
    protected ICrudServicio<Tipodocumento> servicio() {
        return servicio;
    }
}
