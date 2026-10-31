package uis.edu.entornos.veterinaria.servicio;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import uis.edu.entornos.veterinaria.excepcion.BadRequestException;
import uis.edu.entornos.veterinaria.excepcion.ResourceNotFoundException;
import uis.edu.entornos.veterinaria.modelo.EntidadBase;

/**
 * Implementación genérica del CRUD. Cada servicio concreto solo define su repositorio,
 * cómo resolver sus relaciones y sus reglas de negocio.
 */
@Transactional
public abstract class CrudServicioBase<T extends EntidadBase> implements ICrudServicio<T> {

    protected abstract JpaRepository<T, Long> repositorio();

    /** Nombre legible de la entidad para los mensajes de error, por ejemplo "Cliente". */
    protected abstract String nombreEntidad();

    /** Reemplaza las referencias que llegan solo con id por las entidades completas de la base de datos. */
    protected void prepararRelaciones(T entidad) {
    }

    /** Reglas de negocio. idActual es null cuando se está creando. */
    protected void validar(T entidad, Long idActual) {
    }

    /** Propiedades que no se copian al actualizar. */
    protected String[] camposIgnorados() {
        return new String[] { "id" };
    }

    @Override
    public List<T> listar() {
        return repositorio().findAll();
    }

    @Override
    public T buscar(Long id) {
        return repositorio().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(nombreEntidad() + " no encontrado con el id: " + id));
    }

    @Override
    public T crear(T entidad) {
        entidad.setId(null);
        prepararRelaciones(entidad);
        validar(entidad, null);
        return repositorio().saveAndFlush(entidad);
    }

    @Override
    public T actualizar(Long id, T entidad) {
        T existente = repositorio().findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No se puede actualizar. " + nombreEntidad() + " no encontrado con el id: " + id));
        prepararRelaciones(entidad);
        validar(entidad, id);
        BeanUtils.copyProperties(entidad, existente, camposIgnorados());
        return repositorio().saveAndFlush(existente);
    }

    @Override
    public void eliminar(Long id) {
        T existente = repositorio().findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No se puede eliminar. " + nombreEntidad() + " no encontrado con el id: " + id));
        repositorio().delete(existente);
        repositorio().flush();
    }

    /**
     * Busca en la base de datos la entidad referenciada por su id.
     * Si la referencia es obligatoria y no viene, lanza BadRequest; si el id no existe, lanza NotFound.
     */
    protected <R extends EntidadBase> R resolver(R referencia, JpaRepository<R, Long> repo, String nombre, boolean obligatoria) {
        if (referencia == null || referencia.getId() == null) {
            if (obligatoria) {
                throw new BadRequestException("El campo " + nombre + " es obligatorio");
            }
            return null;
        }
        return repo.findById(referencia.getId()).orElseThrow(
                () -> new ResourceNotFoundException("El registro de " + nombre + " con ID " + referencia.getId() + " no existe."));
    }
}
