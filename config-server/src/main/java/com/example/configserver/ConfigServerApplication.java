/*
 * Clase principal para arrancar la aplicación Config Server.
 *
 * Esta clase contiene el método main que inicia el servidor de configuración
 * centralizado usando Spring Cloud Config Server, permitiendo la gestión
 * de configuraciones externas para otros microservicios.
 * 
 * @author Ramos
 */
package com.example.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
