package uis.edu.entornos.veterinaria.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Cliente;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByIdTipoDocumento_IdAndNumeroDocumento(Long idTipoDocumento, String numeroDocumento);

    Optional<Cliente> findByEmailIgnoreCase(String email);
}
