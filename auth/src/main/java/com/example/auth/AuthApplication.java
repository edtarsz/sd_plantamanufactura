package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal de la aplicación de autenticación.
 * 
 * Esta clase configura y arranca la aplicación Spring Boot.
 * 
 * Anotaciones usadas:
 * - @SpringBootApplication: Marca esta clase como la configuración principal de Spring Boot.
 * - @EnableDiscoveryClient: Habilita el cliente para el registro y descubrimiento de servicios (Eureka, Consul, etc.).
 * - @EnableFeignClients: Habilita el uso de Feign para realizar llamadas HTTP a otros microservicios declarativamente.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuthApplication {

    /**
     * Punto de entrada de la aplicación.
     * Ejecuta la aplicación Spring Boot.
     * 
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
