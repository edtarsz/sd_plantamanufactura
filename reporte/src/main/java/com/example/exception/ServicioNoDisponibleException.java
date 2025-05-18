/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;

/**
 *
 * @author Ramos
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String servicio) {
        super("Servicio no disponible: " + servicio);
    }
}
