package uis.edu.entornos.veterinaria.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "mascota")
public class Mascota extends EntidadBase {

    @NotNull(message = "El cliente (dueño) es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente idCliente;

    @NotNull(message = "La raza es obligatoria")
    @ManyToOne
    @JoinColumn(name = "idRaza", nullable = false)
    private Raza idRaza;

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @NotBlank(message = "El sexo es obligatorio (M = macho, H = hembra)")
    @Pattern(regexp = "^[MmHh]$", message = "El sexo debe ser M (macho) o H (hembra)")
    @Column(name = "sexo", nullable = false, length = 1)
    private String sexo;

    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;

    @Positive(message = "El peso debe ser mayor a cero")
    @Column(name = "pesoKg", precision = 6, scale = 2)
    private BigDecimal pesoKg;

    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
