/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.piezas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Pieza.
 *
 * Esta interfaz proporciona métodos para realizar operaciones CRUD sobre la
 * tabla correspondiente a la entidad Pieza en la base de datos.
 *
 * También se pueden declarar métodos personalizados si se requiere lógica
 * adicional de consulta.
 *
 * Anotación @Repository permite a Spring detectar esta interfaz como un
 * componente de acceso a datos.
 *
 * @author Ramos
 */
@Repository
public interface PiezaRepository extends JpaRepository<Pieza, Long> {
}
