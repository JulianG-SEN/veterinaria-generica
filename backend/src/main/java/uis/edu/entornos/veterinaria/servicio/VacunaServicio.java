package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;

@Service
public class VacunaServicio extends CrudServicioBase<Vacuna> {

    @Autowired
    private VacunaRepositorio repositorio;

    @Autowired
    private EspecieRepositorio especieRepositorio;

    @Override
    protected JpaRepository<Vacuna, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Vacuna";
    }

    @Override
    protected void prepararRelaciones(Vacuna vacuna) {
        vacuna.setIdEspecie(resolver(vacuna.getIdEspecie(), especieRepositorio, "especie", true));
    }
}
