package com.example.notificaciones.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
/**
 * Configuración de WebSockets con STOMP para la aplicación.
 *
 * <p>Esta clase habilita el uso de WebSockets y define los endpoints y
 * el broker de mensajes para la comunicación en tiempo real utilizando el protocolo STOMP.</p>
 *
 * <p>Se habilita un endpoint accesible en <code>/ws</code> con soporte para SockJS,
 * lo que permite compatibilidad con navegadores que no soportan WebSocket nativamente.</p>
 *
 * <p>También se configura un broker de mensajes simple para gestionar canales de comunicación 
 * con prefijos como <code>/topic</code> y se define el prefijo <code>/app</code> 
 * para los destinos de mensajes entrantes del cliente.</p>
 *
 * @author crist
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registra el endpoint STOMP que los clientes usarán para conectarse al WebSocket.
     *
     * <p>Se expone el endpoint <code>/ws</code> y se habilita SockJS como mecanismo de reserva.</p>
     *
     * @param registry el registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        System.out.println("✅ Registrando endpoint WebSocket: ws://localhost:8084/ws-notificaciones");
    }

    /**
     * Configura el broker de mensajes para habilitar la comunicación entre clientes y servidor.
     *
     * <p>Los mensajes enviados desde el cliente deben ir precedidos de <code>/app</code>,
     * y los suscriptores pueden escuchar canales bajo <code>/topic</code>.</p>
     *
     * @param registry el registro del broker de mensajes
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}
