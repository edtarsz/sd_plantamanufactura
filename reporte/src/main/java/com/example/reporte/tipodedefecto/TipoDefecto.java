/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa un tipo de defecto en la base de datos.
 *
 * Esta clase se mapea a la tabla "tipo_defecto"
 *
 * Se utiliza Lombok para reducir la verbosidad del código (getters, setters,
 * etc.).
 *
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tipo_defecto")
public class TipoDefecto {

    /**
     * Clave primaria del tipo de defecto. Se genera automáticamente con
     * estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoDefecto")
    private Long idTipoDefecto;

    /**
     * Nombre del tipo de defecto. Campo obligatorio y único.
     */
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

}
