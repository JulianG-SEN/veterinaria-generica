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
@Table(name = "raza", uniqueConstraints = @UniqueConstraint(name = "UQ_RAZA_ESPECIE_NOMBRE", columnNames = { "idEspecie", "nombre" }))
public class Raza extends EntidadBase {

    @NotNull(message = "La especie es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idEspecie", nullable = false)
    private Especie idEspecie;

    @NotBlank(message = "El nombre de la raza es obligatorio")
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;
}
