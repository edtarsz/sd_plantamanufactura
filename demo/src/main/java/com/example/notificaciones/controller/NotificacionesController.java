package com.example.notificaciones.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.example.notificaciones.model.Notificacion;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
/**
 * Controlador que gestiona el canal de notificaciones vía WebSocket.
 *
 * <p>Recibe mensajes enviados desde el cliente a través del destino <code>/app/notify</code>
 * y los redirige al topic <code>/topic/notifications</code> para su difusión
 * a todos los suscriptores.</p>
 *
 * <p>Esta clase utiliza anotaciones de Spring Messaging para implementar la lógica
 * de mensajería en tiempo real.</p>
 *
 * @author crist
 */
@Controller
public class NotificacionesController {

    /**
     * Método que recibe una notificación desde el cliente y la reenvía a los suscriptores.
     *
     * <p>El cliente debe enviar mensajes a <code>/app/notify</code>.
     * Todos los clientes suscritos a <code>/topic/notifications</code> recibirán el mensaje.</p>
     *
     * @param notificacion objeto que contiene el mensaje a enviar
     * @return la misma notificación recibida, que será difundida
     */
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notificacion sendNotification(Notificacion notificacion) {
        System.out.println("📬 Mensaje recibido en el backend: " + notificacion.getMessage());
        return notificacion;
    }
}

