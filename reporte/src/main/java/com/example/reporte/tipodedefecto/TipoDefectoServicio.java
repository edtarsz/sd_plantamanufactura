/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import com.example.reporte.defectos.DefectoRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Servicio que maneja la lógica de negocio relacionada con los tipos de
 * defectos. Permite obtener, guardar/actualizar y eliminar registros de tipo de
 * defecto, asegurando la integridad de los datos.
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class TipoDefectoServicio {

    // Repositorio para acceder a los datos de tipo de defecto
     private final TipoDefectoRepository tipoDefectoRepository;

    // Repositorio para acceder a los defectos, usado para validaciones antes de eliminar
    private final DefectoRepository defectoRepositorio;
    

    /**
     * Obtiene todos los tipos de defecto existentes en la base de datos.
     *
     * @return una lista de objetos TipoDefecto
     */
    public List<TipoDefecto> getTipoDefectos() {
        return tipoDefectoRepository.findAll();
    }

    /**
     * Obtiene un tipo de defecto específico por su ID.
     *
     * @param id identificador del tipo de defecto
     * @return un Optional con el tipo de defecto si existe
     */
    public Optional<TipoDefecto> getTipoDefecto(Long id) {
        return tipoDefectoRepository.findById(id);
    }

    /**
     * Guarda un nuevo tipo de defecto o actualiza uno existente. Antes de
     * guardar, normaliza el nombre (mayúsculas y sin espacios). Valida que no
     * exista otro tipo de defecto con el mismo nombre.
     *
     * @param tipoDefecto objeto TipoDefecto a guardar o actualizar
     * @return el objeto guardado
     * @throws DataIntegrityViolationException si el nombre ya existe
     */
    public TipoDefecto saveOrUpdate(TipoDefecto tipoDefecto) {
        // Normalizar el nombre a mayúsculas sin espacios al inicio/final
        String nombreNormalizado = tipoDefecto.getNombre().trim().toUpperCase();
        tipoDefecto.setNombre(nombreNormalizado);

        // Si es una actualización, verificar que no exista otro con el mismo nombre
        if (tipoDefecto.getIdTipoDefecto() != null) {
            Optional<TipoDefecto> existente = tipoDefectoRepository.findByNombreAndIdTipoDefectoNot(
                    tipoDefecto.getNombre(),
                    tipoDefecto.getIdTipoDefecto()
            );

            if (existente.isPresent()) {
                throw new DataIntegrityViolationException("Ya existe un tipo de defecto con ese nombre");
            }
        }

        // Guardar el nuevo o actualizado tipo de defecto
        return tipoDefectoRepository.save(tipoDefecto);
    }

    /**
     * Elimina un tipo de defecto por su ID si no tiene defectos asociados.
     *
     * @param idTipoDefecto identificador del tipo de defecto a eliminar
     * @throws DataIntegrityViolationException si hay defectos asociados
     */
    public void delete(Long idTipoDefecto) {
        // Validar que no existan defectos asociados al tipo
        if (defectoRepositorio.existsByTipoDefectoIdTipoDefecto(idTipoDefecto)) {
            throw new DataIntegrityViolationException("No se puede eliminar porque existen defectos asociados");
        }

        // Eliminar el tipo de defecto
        tipoDefectoRepository.deleteById(idTipoDefecto);
    }
}
