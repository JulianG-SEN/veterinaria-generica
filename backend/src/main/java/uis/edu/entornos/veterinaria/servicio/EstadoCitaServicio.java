package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;

@Service
public class EstadoCitaServicio extends CrudServicioBase<Estadocita> {

    @Autowired
    private EstadoCitaRepositorio repositorio;

    @Override
    protected JpaRepository<Estadocita, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Estado de cita";
    }
}
