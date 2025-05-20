/*
 * Clase principal para arrancar la aplicación Gateway.
 *
 * Esta clase contiene el método main que inicia el contexto de Spring Boot,
 * lanzando el servicio gateway que manejará las peticiones y el enrutamiento.
 * 
 * @author Ramos
 */
package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
