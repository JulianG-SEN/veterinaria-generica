package uis.edu.entornos.veterinaria.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Vacunacion;

@Repository
public interface VacunacionRepositorio extends JpaRepository<Vacunacion, Long> {

    List<Vacunacion> findByIdMascota_IdOrderByFechaAplicacionDesc(Long idMascota);
}
