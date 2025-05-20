/*
 * Clase principal para iniciar la aplicación Frontend del sistema de planta de manufactura.
 *
 * Esta clase habilita el cliente de descubrimiento para que este servicio
 * pueda registrarse en un servidor Eureka o similar, permitiendo que otros servicios lo descubran.
 * 
 * @author Ramos
 */
package com.example.plantamanufactura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

}
