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
@Table(name = "estadocita")
public class Estadocita extends EntidadBase {

    @NotBlank(message = "El nombre del estado es obligatorio")
    @Size(max = 30, message = "El nombre no puede exceder 30 caracteres")
    @Column(name = "nombre", nullable = false, length = 30, unique = true)
    private String nombre;
}
