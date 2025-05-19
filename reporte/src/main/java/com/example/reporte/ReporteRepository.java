/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ramos
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long>, JpaSpecificationExecutor<Reporte> {

    @Query("SELECT r FROM Reporte r WHERE r.idUsuario = :idUsuario")
    List<Reporte> findAllByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("SELECT r FROM Reporte r "
            + "LEFT JOIN FETCH r.defectos d "
            + "LEFT JOIN FETCH d.tipoDefecto "
            + "WHERE r.idUsuario = :usuarioId")
    List<Reporte> findByUsuarioIdWithDefectos(@Param("usuarioId") Long usuarioId);

    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.defectos")
    List<Reporte> findAllWithDefectos();
}
