/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.defectos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase DTO (Data Transfer Object) que representa la información de un defecto
 * para transferir datos entre capas de la aplicación. Contiene detalles como el
 * tipo de defecto, cantidad de piezas afectadas, costo asociado y detalles
 * adicionales.
 *
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefectoDTO {

    private String tipoDefecto;       // Nombre o tipo del defecto
    private Integer cantidad_piezas;  // Número de piezas afectadas
    private Double costo;             // Costo total asociado al defecto
    private String detalles;          // Información adicional o descripción del defecto

}
