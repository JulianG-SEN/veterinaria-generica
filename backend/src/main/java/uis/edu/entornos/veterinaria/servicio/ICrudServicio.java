package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

/** Contrato CRUD común a todos los servicios (equivale a IClienteServicio del proyecto base). */
public interface ICrudServicio<T> {

    List<T> listar();

    T buscar(Long id);

    T crear(T entidad);

    T actualizar(Long id, T entidad);

    void eliminar(Long id);
}
