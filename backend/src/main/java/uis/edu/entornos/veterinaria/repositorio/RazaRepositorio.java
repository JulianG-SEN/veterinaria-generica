package uis.edu.entornos.veterinaria.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Raza;

@Repository
public interface RazaRepositorio extends JpaRepository<Raza, Long> {

    List<Raza> findByIdEspecie_Id(Long idEspecie);
}
