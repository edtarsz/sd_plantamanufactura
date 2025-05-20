/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.piezas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una pieza dentro del sistema.
 *
 * Esta clase se mapea a la tabla "pieza" en la base de datos y contiene la
 * información básica sobre una pieza, como su identificador, nombre y costo. Se
 * utiliza en operaciones de persistencia con JPA.
 *
 * Los campos están anotados con JPA para definir sus restricciones y
 * correspondencia con columnas de la base de datos.
 *
 * También se utilizan anotaciones de Lombok para generar automáticamente
 * métodos como getters, setters, constructores y el patrón builder.
 *
 * Esta clase es usada por los servicios, controladores y repositorios que
 * manejan la lógica y las operaciones relacionadas con piezas.
 *
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pieza")
public class Pieza {

    /**
     * Identificador único de la pieza. Se genera automáticamente al insertar
     * una nueva pieza.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPieza")
    private Long idPieza;

    /**
     * Nombre de la pieza. Es obligatorio y debe ser único dentro del sistema.
     */
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    /**
     * Costo de la pieza. Es obligatorio y debe ser único dentro del sistema.
     */
    @Column(name = "costo", nullable = false, unique = true)
    private Float costo;
}
