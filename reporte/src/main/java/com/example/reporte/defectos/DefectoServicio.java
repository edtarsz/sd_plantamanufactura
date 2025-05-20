/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.defectos;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de manejar la lógica de negocio relacionada con los
 * defectos.
 *
 * Esta clase actúa como intermediario entre el controlador y el repositorio,
 * encapsulando las operaciones relacionadas con la entidad Defecto. Proporciona
 * métodos para obtener, guardar, actualizar y eliminar defectos.
 *
 * Se utiliza la anotación @Service para indicar que esta clase forma parte de
 * la capa de servicio dentro de la arquitectura de la aplicación.
 *
 * El repositorio se inyecta automáticamente para interactuar con la base de
 * datos.
 *
 * Esta clase facilita la reutilización y centralización de la lógica
 * relacionada con la entidad Defecto.
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class DefectoServicio {

    /**
     * Repositorio que proporciona acceso a la base de datos para la entidad
     * Defecto.
     */
    @Autowired
    DefectoRepository defectoRepository;

    /**
     * Obtiene una lista con todos los defectos registrados en el sistema.
     *
     * @return lista de objetos Defecto
     */
    public List<Defecto> getDefectos() {
        return defectoRepository.findAll();
    }

    /**
     * Busca un defecto específico por su identificador único.
     *
     * @param id identificador del defecto
     * @return un Optional que puede contener el defecto si se encuentra
     */
    public Optional<Defecto> getDefecto(Long id) {
        return defectoRepository.findById(id);
    }

    /**
     * Guarda un nuevo defecto o actualiza uno existente si ya tiene un ID
     * asignado.
     *
     * @param defecto objeto Defecto a guardar o actualizar
     */
    public void saveOrUpdate(Defecto defecto) {
        defectoRepository.save(defecto);
    }

    /**
     * Elimina un defecto de la base de datos según su identificador.
     *
     * @param id identificador del defecto a eliminar
     */
    public void delete(Long id) {
        defectoRepository.deleteById(id);
    }
}
