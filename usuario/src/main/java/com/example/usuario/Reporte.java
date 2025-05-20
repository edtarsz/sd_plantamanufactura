/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa un reporte individual.
 *
 * <p>Contiene únicamente el costo total del reporte, aunque puede ampliarse
 * en el futuro con más información.</p>
 * 
 * <p>Utiliza Lombok para generar constructor, getter y setter.</p>
 * 
 * @author Ramos
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reporte {

    /**
     * Costo total del reporte.
     */
    private Float costoTotal;
}
