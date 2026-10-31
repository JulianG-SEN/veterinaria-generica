package uis.edu.entornos.veterinaria.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Personal de la clínica que inicia sesión (administrador, veterinario, recepcionista). */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USUARIO_DOCUMENTO", columnNames = { "idTipoDocumento", "numeroDocumento" }),
        @UniqueConstraint(name = "UQ_USUARIO_NOMBREUSUARIO", columnNames = { "nombreUsuario" }),
        @UniqueConstraint(name = "UQ_USUARIO_EMAIL", columnNames = { "email" }) })
public class Usuario extends EntidadBase {

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

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    @Column(name = "nombreUsuario", nullable = false, length = 50)
    private String nombreUsuario;

    /** Solo se recibe (WRITE_ONLY): nunca se devuelve en las respuestas. Se guarda cifrada con BCrypt. */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idRol", nullable = false)
    private Rol idRol;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
