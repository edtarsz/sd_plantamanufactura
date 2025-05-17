/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ramos
 */
@Repository
public interface TipoDefectoRepository extends JpaRepository<TipoDefecto, Long> {

    Optional<TipoDefecto> findByNombreAndIdTipoDefectoNot(String nombre, Long id);
}
