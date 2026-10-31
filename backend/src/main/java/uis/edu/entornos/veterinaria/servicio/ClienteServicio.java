package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.modelo.*;
import uis.edu.entornos.veterinaria.repositorio.*;

@Service
public class ClienteServicio extends CrudServicioBase<Cliente> {

    @Autowired
    private ClienteRepositorio repositorio;

    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepositorio;

    @Override
    protected JpaRepository<Cliente, Long> repositorio() {
        return repositorio;
    }

    @Override
    protected String nombreEntidad() {
        return "Cliente";
    }

    @Override
    protected void prepararRelaciones(Cliente cliente) {
        cliente.setIdTipoDocumento(resolver(cliente.getIdTipoDocumento(), tipoDocumentoRepositorio, "tipo de documento", true));
    }

    @Override
    protected void validar(Cliente cliente, Long idActual) {
        repositorio.findByIdTipoDocumento_IdAndNumeroDocumento(cliente.getIdTipoDocumento().getId(), cliente.getNumeroDocumento())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new BadRequestException("Ya existe un cliente registrado con el número de documento: " + cliente.getNumeroDocumento());
                });

        repositorio.findByEmailIgnoreCase(cliente.getEmail())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new BadRequestException("Ya existe un cliente registrado con el correo electrónico: " + cliente.getEmail());
                });
    }
}
