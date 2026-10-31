package uis.edu.entornos.veterinaria.repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Cita;

@Repository
public interface CitaRepositorio extends JpaRepository<Cita, Long> {

    Optional<Cita> findByIdVeterinario_IdAndFechaHora(Long idVeterinario, LocalDateTime fechaHora);

    List<Cita> findByIdVeterinario_IdAndFechaHoraBetweenOrderByFechaHora(Long idVeterinario, LocalDateTime desde, LocalDateTime hasta);
}
