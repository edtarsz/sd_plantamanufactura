package com.example.notificaciones.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author crist
 */
import com.example.notificaciones.model.Notificacion;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificacionesController {

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notificacion sendNotification(Notificacion notificacion) {
        System.out.println("📬 Mensaje recibido en el backend: " + notificacion.getMessage());
        return notificacion;
    }
}

