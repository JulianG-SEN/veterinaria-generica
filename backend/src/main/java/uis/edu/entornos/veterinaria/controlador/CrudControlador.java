package uis.edu.entornos.veterinaria.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import uis.edu.entornos.veterinaria.excepcion.ErrorDetalle;
import uis.edu.entornos.veterinaria.modelo.EntidadBase;
import uis.edu.entornos.veterinaria.servicio.ICrudServicio;

/**
 * Controlador CRUD genérico: GET /list, GET /list/{id}, POST, PUT /{id}, DELETE /{id}.
 * Cada controlador concreto solo declara su ruta (@RequestMapping), su etiqueta de Swagger y su servicio.
 */
public abstract class CrudControlador<T extends EntidadBase> {

    protected abstract ICrudServicio<T> servicio();

    @Operation(summary = "Obtener lista de registros", description = "Retorna todos los registros almacenados en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/list")
    public List<T> listar() {
        return servicio().listar();
    }

    @Operation(summary = "Obtener registro por ID", description = "Busca y retorna un registro especificando su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class)))
    })
    @GetMapping("/list/{id}")
    public ResponseEntity<T> buscarPorId(
            @Parameter(description = "ID del registro a buscar", required = true) @PathVariable("id") Long id) {
        return new ResponseEntity<>(servicio().buscar(id), HttpStatus.OK);
    }

    @Operation(summary = "Registrar un nuevo registro", description = "Crea un registro validando los datos y las reglas de negocio.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registro creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class))),
        @ApiResponse(responseCode = "404", description = "Una referencia (llave foránea) no existe",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class)))
    })
    @PostMapping({ "", "/" })
    public ResponseEntity<T> agregar(@Valid @RequestBody T entidad) {
        return new ResponseEntity<>(servicio().crear(entidad), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un registro", description = "Actualiza la información de un registro existente por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class))),
        @ApiResponse(responseCode = "404", description = "Registro o referencia no encontrada",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<T> editar(
            @Parameter(description = "ID del registro a actualizar", required = true) @PathVariable("id") Long id,
            @Valid @RequestBody T entidad) {
        return new ResponseEntity<>(servicio().actualizar(id, entidad), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un registro", description = "Elimina un registro dado su ID (falla si está relacionado con otros datos).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente"),
        @ApiResponse(responseCode = "400", description = "El registro está en uso por otros datos",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class))),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                     content = @Content(schema = @Schema(implementation = ErrorDetalle.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del registro a eliminar", required = true) @PathVariable("id") Long id) {
        servicio().eliminar(id);
        return new ResponseEntity<>("Registro eliminado exitosamente con el ID: " + id, HttpStatus.OK);
    }
}
