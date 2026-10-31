package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;
import java.time.LocalDateTime;

@Service
public class HistoriaClinicaServicio extends CrudServicioBase<Historiaclinica> {

    @Autowired
    private HistoriaClinicaRepositorio repositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private CitaRepositorio citaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    protected JpaRepository<Historiaclinica, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Historia clínica";
    }

    @Override
    protected void prepararRelaciones(Historiaclinica historia) {
        historia.setIdMascota(resolver(historia.getIdMascota(), mascotaRepositorio, "mascota", true));
        historia.setIdCita(resolver(historia.getIdCita(), citaRepositorio, "cita", false));
        historia.setIdVeterinario(resolver(historia.getIdVeterinario(), usuarioRepositorio, "veterinario", true));
        if (historia.getFecha() == null) {
            historia.setFecha(LocalDateTime.now());
        }
    }

    @Override
    protected void validar(Historiaclinica historia, Long idActual) {
        if (!UsuarioServicio.esVeterinario(historia.getIdVeterinario())) {
            throw new BadRequestException("El usuario seleccionado no tiene el rol de veterinario");
        }
        if (historia.getIdCita() != null
                && !historia.getIdCita().getIdMascota().getId().equals(historia.getIdMascota().getId())) {
            throw new BadRequestException("La cita seleccionada no corresponde a la mascota indicada");
        }
    }

    public List<Historiaclinica> porMascota(Long idMascota) {
        return repositorio.findByIdMascota_IdOrderByFechaDesc(idMascota);
    }
}
