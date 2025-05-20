/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad TipoDefecto. Proporciona métodos CRUD y una
 * consulta personalizada para verificar duplicidad de nombres.
 *
 * También incluye una consulta personalizada para verificar si existe otro tipo
 * de defecto con el mismo nombre, pero distinto ID (usado en validaciones).
 *
 * @author Ramos
 */
@Repository
public interface TipoDefectoRepository extends JpaRepository<TipoDefecto, Long> {

    /**
     * Busca un tipo de defecto por nombre, excluyendo un ID específico.
     *
     * Este método es útil para validar que no haya duplicados al momento de
     * actualizar un tipo de defecto (es decir, otro registro con el mismo
     * nombre, pero diferente ID).
     *
     * @param nombre nombre del tipo de defecto normalizado
     * @param id ID que debe ser excluido de la búsqueda
     * @return un Optional con el tipo de defecto encontrado (si existe)
     */
    Optional<TipoDefecto> findByNombreAndIdTipoDefectoNot(String nombre, Long id);
}
