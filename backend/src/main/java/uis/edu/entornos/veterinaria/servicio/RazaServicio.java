package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;

@Service
public class RazaServicio extends CrudServicioBase<Raza> {

    @Autowired
    private RazaRepositorio repositorio;

    @Autowired
    private EspecieRepositorio especieRepositorio;

    @Override
    protected JpaRepository<Raza, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Raza";
    }

    @Override
    protected void prepararRelaciones(Raza raza) {
        raza.setIdEspecie(resolver(raza.getIdEspecie(), especieRepositorio, "especie", true));
    }

    public List<Raza> porEspecie(Long idEspecie) {
        return repositorio.findByIdEspecie_Id(idEspecie);
    }
}
