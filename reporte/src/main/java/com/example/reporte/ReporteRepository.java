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

/*
 * Repositorio JPA para la entidad Reporte.
 *
 * Esta interfaz extiende JpaRepository y JpaSpecificationExecutor,
 * proporcionando métodos estándar para operaciones CRUD y consultas
 * avanzadas con criterios dinámicos.
 *
 * Contiene consultas personalizadas para obtener reportes filtrados
 * por usuario, incluyendo relaciones con defectos y tipos de defectos,
 * así como para obtener listas únicas de inspectores y lotes.
 *
 * Las consultas usan JPQL para optimizar la carga y evitar problemas
 * de N+1 al hacer fetch joins con colecciones relacionadas.
 * 
 * @author Ramos
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long>, JpaSpecificationExecutor<Reporte> {

    /**
     * Obtiene todos los reportes asociados a un usuario dado por su ID.
     *
     * @param idUsuario ID del usuario
     * @return Lista de reportes del usuario
     */
    @Query("SELECT r FROM Reporte r WHERE r.idUsuario = :idUsuario")
    List<Reporte> findAllByUsuarioId(@Param("idUsuario") Long idUsuario);

    /**
     * Obtiene reportes de un usuario específico, cargando también la colección
     * de defectos y el tipo de defecto para evitar múltiples consultas.
     *
     * @param usuarioId ID del usuario
     * @return Lista de reportes con sus defectos y tipos de defecto
     */
    @Query("SELECT r FROM Reporte r "
            + "LEFT JOIN FETCH r.defectos d "
            + "LEFT JOIN FETCH d.tipoDefecto "
            + "WHERE r.idUsuario = :usuarioId")
    List<Reporte> findByUsuarioIdWithDefectos(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene todos los reportes con sus defectos relacionados cargados.
     *
     * @return Lista de todos los reportes con defectos cargados
     */
    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.defectos")
    List<Reporte> findAllWithDefectos();

    /**
     * Obtiene una lista de inspectores únicos ordenados alfabéticamente.
     *
     * @return Lista de nombres únicos de inspectores
     */
    @Query("SELECT DISTINCT r.inspector FROM Reporte r ORDER BY r.inspector")
    List<String> findInspectoresUnicos();

    /**
     * Obtiene una lista de lotes únicos ordenados alfabéticamente.
     *
     * @return Lista de IDs de lotes únicos
     */
    @Query("SELECT DISTINCT r.loteId FROM Reporte r ORDER BY r.loteId")
    List<String> findLotesUnicos();
}
