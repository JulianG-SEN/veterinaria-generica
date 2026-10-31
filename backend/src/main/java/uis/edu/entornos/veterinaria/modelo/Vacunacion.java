package uis.edu.entornos.veterinaria.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vacunacion")
public class Vacunacion extends EntidadBase {

    @NotNull(message = "La mascota es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idMascota", nullable = false)
    private Mascota idMascota;

    @NotNull(message = "La vacuna es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idVacuna", nullable = false)
    private Vacuna idVacuna;

    @NotNull(message = "El veterinario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idVeterinario", nullable = false)
    private Usuario idVeterinario;

    @NotNull(message = "La fecha de aplicación es obligatoria")
    @Column(name = "fechaAplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "proximaDosis")
    private LocalDate proximaDosis;
}
