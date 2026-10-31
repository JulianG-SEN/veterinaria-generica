package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;

@Service
public class VacunacionServicio extends CrudServicioBase<Vacunacion> {

    @Autowired
    private VacunacionRepositorio repositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private VacunaRepositorio vacunaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    protected JpaRepository<Vacunacion, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Vacunación";
    }

    @Override
    protected void prepararRelaciones(Vacunacion vacunacion) {
        vacunacion.setIdMascota(resolver(vacunacion.getIdMascota(), mascotaRepositorio, "mascota", true));
        vacunacion.setIdVacuna(resolver(vacunacion.getIdVacuna(), vacunaRepositorio, "vacuna", true));
        vacunacion.setIdVeterinario(resolver(vacunacion.getIdVeterinario(), usuarioRepositorio, "veterinario", true));
    }

    @Override
    protected void validar(Vacunacion vacunacion, Long idActual) {
        if (!UsuarioServicio.esVeterinario(vacunacion.getIdVeterinario())) {
            throw new BadRequestException("El usuario seleccionado no tiene el rol de veterinario");
        }
        Long especieVacuna = vacunacion.getIdVacuna().getIdEspecie().getId();
        Long especieMascota = vacunacion.getIdMascota().getIdRaza().getIdEspecie().getId();
        if (!especieVacuna.equals(especieMascota)) {
            throw new BadRequestException("La vacuna seleccionada no corresponde a la especie de la mascota");
        }
        if (vacunacion.getProximaDosis() != null && vacunacion.getProximaDosis().isBefore(vacunacion.getFechaAplicacion())) {
            throw new BadRequestException("La próxima dosis no puede ser anterior a la fecha de aplicación");
        }
    }

    public List<Vacunacion> porMascota(Long idMascota) {
        return repositorio.findByIdMascota_IdOrderByFechaAplicacionDesc(idMascota);
    }
}
