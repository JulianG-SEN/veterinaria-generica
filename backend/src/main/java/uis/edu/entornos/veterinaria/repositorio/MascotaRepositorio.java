package uis.edu.entornos.veterinaria.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.edu.entornos.veterinaria.modelo.Mascota;

@Repository
public interface MascotaRepositorio extends JpaRepository<Mascota, Long> {

    List<Mascota> findByIdCliente_Id(Long idCliente);
}
