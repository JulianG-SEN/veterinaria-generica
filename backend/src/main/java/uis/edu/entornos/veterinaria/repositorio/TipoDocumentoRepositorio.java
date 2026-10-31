package uis.edu.entornos.veterinaria.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Tipodocumento;

@Repository
public interface TipoDocumentoRepositorio extends JpaRepository<Tipodocumento, Long> {
}
