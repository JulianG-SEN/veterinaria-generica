package uis.edu.entornos.veterinaria.repositorio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdTipoDocumento_IdAndNumeroDocumento(Long idTipoDocumento, String numeroDocumento);

    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findByIdRol_NombreIgnoreCaseAndActivoTrue(String nombreRol);
}
