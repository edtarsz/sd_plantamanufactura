package com.example.notificaciones.model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
import lombok.Data;

/**
 * Clase de modelo que representa una notificación.
 *
 * <p>Contiene un único campo <code>message</code> que representa el contenido
 * de la notificación que será enviada a través del canal WebSocket.</p>
 *
 * <p>Actualmente, el método <code>getMessage()</code> lanza una excepción,
 * lo que sugiere que debe implementarse correctamente para que funcione con el controlador.</p>
 *
 * <p>Se recomienda reemplazar la excepción por una implementación real del método getter.</p>
 *
 * @author crist
 */
@Data
public class Notificacion {

    /**
     * Contenido textual de la notificación.
     */
    private String message;

}
