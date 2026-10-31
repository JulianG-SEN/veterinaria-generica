package uis.edu.entornos.veterinaria.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dueño de la mascota (misma estructura que Cliente en el proyecto base). */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cliente", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_CLIENTE_DOCUMENTO", columnNames = { "idTipoDocumento", "numeroDocumento" }),
        @UniqueConstraint(name = "UQ_CLIENTE_EMAIL", columnNames = { "email" }) })
public class Cliente extends EntidadBase {

    @NotNull(message = "El tipo de documento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idTipoDocumento", nullable = false)
    private Tipodocumento idTipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 30, message = "El número de documento no puede exceder 30 caracteres")
    @Column(name = "numeroDocumento", nullable = false, length = 30)
    private String numeroDocumento;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 100, message = "La dirección no puede exceder 100 caracteres")
    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;
}
