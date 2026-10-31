package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class CitaServicio extends CrudServicioBase<Cita> {

    @Autowired
    private CitaRepositorio repositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ServicioRepositorio servicioRepositorio;

    @Autowired
    private EstadoCitaRepositorio estadoCitaRepositorio;

    @Override
    protected JpaRepository<Cita, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Cita";
    }

    @Override
    protected void prepararRelaciones(Cita cita) {
        cita.setIdMascota(resolver(cita.getIdMascota(), mascotaRepositorio, "mascota", true));
        cita.setIdVeterinario(resolver(cita.getIdVeterinario(), usuarioRepositorio, "veterinario", true));
        cita.setIdServicio(resolver(cita.getIdServicio(), servicioRepositorio, "servicio", true));
        cita.setIdEstadoCita(resolver(cita.getIdEstadoCita(), estadoCitaRepositorio, "estado de la cita", true));
    }

    @Override
    protected void validar(Cita cita, Long idActual) {
        Usuario veterinario = cita.getIdVeterinario();
        if (!UsuarioServicio.esVeterinario(veterinario)) {
            throw new BadRequestException("El usuario seleccionado no tiene el rol de veterinario");
        }
        if (!Boolean.TRUE.equals(veterinario.getActivo())) {
            throw new BadRequestException("El veterinario seleccionado está inactivo");
        }
        if (!Boolean.TRUE.equals(cita.getIdMascota().getActivo())) {
            throw new BadRequestException("La mascota seleccionada está inactiva");
        }
        if (!Boolean.TRUE.equals(cita.getIdServicio().getActivo())) {
            throw new BadRequestException("El servicio seleccionado está inactivo");
        }

        repositorio.findByIdVeterinario_IdAndFechaHora(veterinario.getId(), cita.getFechaHora())
                .filter(otra -> !otra.getId().equals(idActual))
                .ifPresent(otra -> {
                    throw new BadRequestException("El veterinario ya tiene una cita programada en esa fecha y hora");
                });
    }

    public List<Cita> agenda(Long idVeterinario, LocalDate fecha) {
        return repositorio.findByIdVeterinario_IdAndFechaHoraBetweenOrderByFechaHora(
                idVeterinario, fecha.atStartOfDay(), fecha.atTime(LocalTime.MAX));
    }
}
