/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.defectos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio que proporciona acceso a la base de datos para la entidad
 * Defecto. Extiende JpaRepository para heredar operaciones CRUD básicas y
 * permite definir consultas personalizadas.
 *
 * @author Ramos
 */
@Repository
public interface DefectoRepository extends JpaRepository<Defecto, Long> {

    /**
     * Verifica si existen defectos asociados a un tipo de defecto específico.
     *
     * @param idTipoDefecto ID del tipo de defecto
     * @return true si existen defectos asociados, false en caso contrario
     */
    boolean existsByTipoDefectoIdTipoDefecto(Long idTipoDefecto);
}
