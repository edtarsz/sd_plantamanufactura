package com.examplee;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Clase principal de la aplicación de notificaciones.
 *
 * <p>Inicializa y arranca el contexto de Spring Boot,
 * escaneando los paquetes que pertenecen al namespace <code>com.example</code>.</p>
 *
 * <p>Esta clase define el punto de entrada para ejecutar la aplicación desde la consola
 * o desde un entorno embebido.</p>
 *
 * @author crist
 */
@SpringBootApplication(scanBasePackages = "com.example")
public class NotificacionesApplication {

    /**
     * Método principal que lanza la aplicación Spring Boot.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificacionesApplication.class, args);
    }
}
