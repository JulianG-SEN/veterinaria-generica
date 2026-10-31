package uis.edu.entornos.veterinaria.excepcion;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorDetalle> respuesta(HttpStatus estado, String error, String mensaje, WebRequest request) {
        ErrorDetalle detalle = new ErrorDetalle(LocalDateTime.now(), estado.value(), error, mensaje, request.getDescription(false));
        return new ResponseEntity<>(detalle, estado);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetalle> manejarResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetalle> manejarRecursoEstatico(NoResourceFoundException ex, WebRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", "No existe el recurso solicitado", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetalle> manejarBadRequestException(BadRequestException ex, WebRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "Petición incorrecta", ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetalle> manejarUnauthorized(UnauthorizedException ex, WebRequest request) {
        return respuesta(HttpStatus.UNAUTHORIZED, "No autorizado", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetalle> manejarIntegridad(DataIntegrityViolationException ex, WebRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "Restricción de integridad",
                "La operación no se pudo completar: el registro está relacionado con otros datos o ya existe un valor único duplicado.",
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetalle> manejarCuerpoInvalido(HttpMessageNotReadableException ex, WebRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "Petición incorrecta",
                "El cuerpo de la petición no es válido (revise el formato JSON y las fechas, por ejemplo 2026-11-03T09:00).", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetalle> manejarValidaciones(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        ErrorDetalle errorDetalle = new ErrorDetalle(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de Validación de Datos",
                "Uno o más campos contienen información inválida",
                request.getDescription(false),
                errores
        );
        return new ResponseEntity<>(errorDetalle, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetalle> manejarExcepcionGeneral(Exception ex, WebRequest request) {
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno del Servidor", ex.getMessage(), request);
    }
}
