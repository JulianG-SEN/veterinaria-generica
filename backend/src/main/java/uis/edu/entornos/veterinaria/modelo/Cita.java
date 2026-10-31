package uis.edu.entornos.veterinaria.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cita", uniqueConstraints = @UniqueConstraint(name = "UQ_CITA_VETERINARIO_FECHA", columnNames = { "idVeterinario", "fechaHora" }))
public class Cita extends EntidadBase {

    @NotNull(message = "La mascota es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idMascota", nullable = false)
    private Mascota idMascota;

    /** Usuario con rol VETERINARIO. */
    @NotNull(message = "El veterinario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idVeterinario", nullable = false)
    private Usuario idVeterinario;

    @NotNull(message = "El servicio es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idServicio", nullable = false)
    private Servicio idServicio;

    @NotNull(message = "El estado de la cita es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idEstadoCita", nullable = false)
    private Estadocita idEstadoCita;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    @Column(name = "motivo", length = 255)
    private String motivo;
}
