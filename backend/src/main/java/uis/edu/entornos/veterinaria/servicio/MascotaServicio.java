package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;

@Service
public class MascotaServicio extends CrudServicioBase<Mascota> {

    @Autowired
    private MascotaRepositorio repositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private RazaRepositorio razaRepositorio;

    @Override
    protected JpaRepository<Mascota, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Mascota";
    }

    @Override
    protected void prepararRelaciones(Mascota mascota) {
        mascota.setIdCliente(resolver(mascota.getIdCliente(), clienteRepositorio, "cliente", true));
        mascota.setIdRaza(resolver(mascota.getIdRaza(), razaRepositorio, "raza", true));
        mascota.setSexo(mascota.getSexo().toUpperCase());
        if (mascota.getActivo() == null) {
            mascota.setActivo(Boolean.TRUE);
        }
    }

    public List<Mascota> porCliente(Long idCliente) {
        return repositorio.findByIdCliente_Id(idCliente);
    }
}
