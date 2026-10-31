package uis.edu.entornos.veterinaria.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tipodocumento")
public class Tipodocumento extends EntidadBase {

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(name = "tipo", nullable = false, length = 30, unique = true)
    private String tipo;
}
