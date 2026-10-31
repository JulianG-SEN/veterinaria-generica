package uis.edu.entornos.veterinaria.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Vacuna;

@Repository
public interface VacunaRepositorio extends JpaRepository<Vacuna, Long> {
}
