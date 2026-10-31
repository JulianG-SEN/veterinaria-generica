package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import uis.edu.entornos.veterinaria.excepcion.UnauthorizedException;

@Service
public class UsuarioServicio extends CrudServicioBase<Usuario> {

    public static final String ROL_VETERINARIO = "VETERINARIO";

    @Autowired
    private UsuarioRepositorio repositorio;

    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected JpaRepository<Usuario, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Usuario";
    }

    /** La contraseña no se copia en la actualización genérica; se maneja aparte en actualizar(). */
    @Override
    protected String[] camposIgnorados() {
        return new String[] { "id", "password" };
    }

    @Override
    protected void prepararRelaciones(Usuario usuario) {
        usuario.setIdTipoDocumento(resolver(usuario.getIdTipoDocumento(), tipoDocumentoRepositorio, "tipo de documento", true));
        usuario.setIdRol(resolver(usuario.getIdRol(), rolRepositorio, "rol", true));
        if (usuario.getActivo() == null) {
            usuario.setActivo(Boolean.TRUE);
        }
    }

    @Override
    protected void validar(Usuario usuario, Long idActual) {
        repositorio.findByIdTipoDocumento_IdAndNumeroDocumento(usuario.getIdTipoDocumento().getId(), usuario.getNumeroDocumento())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new BadRequestException("Ya existe un usuario con el número de documento: " + usuario.getNumeroDocumento());
                });

        repositorio.findByNombreUsuarioIgnoreCase(usuario.getNombreUsuario())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new BadRequestException("El nombre de usuario " + usuario.getNombreUsuario() + " ya está en uso.");
                });

        repositorio.findByEmailIgnoreCase(usuario.getEmail())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new BadRequestException("Ya existe un usuario con el correo electrónico: " + usuario.getEmail());
                });
    }

    @Override
    public Usuario crear(Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return super.crear(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        String nuevaPassword = usuario.getPassword();
        Usuario actualizado = super.actualizar(id, usuario);
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            actualizado.setPassword(passwordEncoder.encode(nuevaPassword));
            actualizado = repositorio.saveAndFlush(actualizado);
        }
        return actualizado;
    }

    public List<Usuario> veterinarios() {
        return repositorio.findByIdRol_NombreIgnoreCaseAndActivoTrue(ROL_VETERINARIO);
    }

    public Usuario autenticar(String nombreUsuario, String password) {
        Usuario usuario = repositorio.findByNombreUsuarioIgnoreCase(nombreUsuario)
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña incorrectos"));
        if (!Boolean.TRUE.equals(usuario.getActivo()) || !passwordEncoder.matches(password, usuario.getPassword())) {
            throw new UnauthorizedException("Usuario o contraseña incorrectos");
        }
        return usuario;
    }

    public static boolean esVeterinario(Usuario usuario) {
        return usuario != null && usuario.getIdRol() != null && ROL_VETERINARIO.equalsIgnoreCase(usuario.getIdRol().getNombre());
    }
}
