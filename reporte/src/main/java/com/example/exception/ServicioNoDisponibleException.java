/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;

/*
 * Excepción personalizada que se lanza cuando un servicio requerido no está disponible.
 *
 * Hereda de RuntimeException para indicar un error en tiempo de ejecución
 * relacionado con la indisponibilidad de un servicio externo o interno.
 * 
 * @author Ramos
 */
public class ServicioNoDisponibleException extends RuntimeException {

    /**
     * Constructor que recibe el nombre del servicio no disponible y construye
     * un mensaje descriptivo para la excepción.
     *
     * @param servicio Nombre o identificador del servicio no disponible
     */
    public ServicioNoDisponibleException(String servicio) {
        super("Servicio no disponible: " + servicio);
    }
}
