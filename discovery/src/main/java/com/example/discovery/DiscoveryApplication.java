/*
 * Clase principal para iniciar el servidor de descubrimiento Eureka.
 *
 * Esta clase habilita el servidor Eureka, que permite el registro y
 * descubrimiento dinámico de servicios dentro de la arquitectura de microservicios.
 * 
 * @author Ramos
 */
package com.example.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }

}
