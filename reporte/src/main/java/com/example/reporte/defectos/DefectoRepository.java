/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.defectos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ramos
 */
@Repository
public interface DefectoRepository extends JpaRepository<Defecto, Long> {
    boolean existsByTipoDefectoIdTipoDefecto(Long idTipoDefecto);
}
