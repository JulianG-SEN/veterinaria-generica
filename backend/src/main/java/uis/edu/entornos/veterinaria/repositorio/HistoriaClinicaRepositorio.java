package uis.edu.entornos.veterinaria.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Historiaclinica;

@Repository
public interface HistoriaClinicaRepositorio extends JpaRepository<Historiaclinica, Long> {

    List<Historiaclinica> findByIdMascota_IdOrderByFechaDesc(Long idMascota);
}
