/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.defectos;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone endpoints para la gestión de defectos.
 *
 * Permite crear, consultar, actualizar y eliminar registros de defectos
 * encontrados durante el proceso de inspección.
 *
 * @author Ramos
 */
@RestController
@RequestMapping(path = "/api/v1/defectos")
public class DefectoControlador {

    private final DefectoServicio defectoServicio;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param defectoServicio Servicio que maneja la lógica de negocio de
     * defectos
     */
    public DefectoControlador(DefectoServicio defectoServicio) {
        this.defectoServicio = defectoServicio;
    }

    /**
     * Obtiene todos los defectos registrados en el sistema.
     *
     * @return Lista de todos los defectos
     */
    @GetMapping
    public List<Defecto> getAll() {
        return defectoServicio.getDefectos();
    }

    /**
     * Obtiene un defecto específico por su ID.
     *
     * @param idDefecto ID del defecto a consultar
     * @return Optional con el defecto si existe
     */
    @GetMapping("/{idDefecto}")
    public Optional<Defecto> getByID(@PathVariable("idDefecto") Long idDefecto) {
        return defectoServicio.getDefecto(idDefecto);
    }

    /**
     * Guarda o actualiza un defecto.
     *
     * @param defecto Objeto defecto a guardar o actualizar
     */
    @PostMapping
    public void saveOrUpdate(@RequestBody Defecto defecto) {
        defectoServicio.saveOrUpdate(defecto);
    }

    /**
     * Elimina un defecto por su ID.
     *
     * @param idDefecto ID del defecto a eliminar
     */
    @DeleteMapping("/{idDefecto}")
    public void delete(@PathVariable("idDefecto") Long idDefecto) {
        defectoServicio.delete(idDefecto);
    }
}
