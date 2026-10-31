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
@Table(name = "vacuna", uniqueConstraints = @UniqueConstraint(name = "UQ_VACUNA_NOMBRE_ESPECIE", columnNames = { "nombre", "idEspecie" }))
public class Vacuna extends EntidadBase {

    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "La especie es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idEspecie", nullable = false)
    private Especie idEspecie;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;
}
