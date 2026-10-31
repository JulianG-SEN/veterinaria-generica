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
@Table(name = "historiaclinica")
public class Historiaclinica extends EntidadBase {

    @NotNull(message = "La mascota es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idMascota", nullable = false)
    private Mascota idMascota;

    /** Opcional: una atención de urgencia puede registrarse sin cita previa. */
    @ManyToOne
    @JoinColumn(name = "idCita")
    private Cita idCita;

    @NotNull(message = "El veterinario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idVeterinario", nullable = false)
    private Usuario idVeterinario;

    /** Si no se envía, se asigna la fecha y hora actual. */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(max = 500, message = "El diagnóstico no puede exceder 500 caracteres")
    @Column(name = "diagnostico", nullable = false, length = 500)
    private String diagnostico;

    @Size(max = 500, message = "El tratamiento no puede exceder 500 caracteres")
    @Column(name = "tratamiento", length = 500)
    private String tratamiento;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(name = "observaciones", length = 500)
    private String observaciones;
}
