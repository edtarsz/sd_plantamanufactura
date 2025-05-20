/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/*
 * Clase de configuración de Spring que define beans para la aplicación.
 *
 * En particular, esta clase proporciona un bean de RestTemplate que está
 * anotado con @LoadBalanced, lo que permite que el RestTemplate use balanceo
 * de carga basado en cliente cuando realiza solicitudes HTTP a otros servicios.
 *
 * Esto es útil en arquitecturas basadas en microservicios donde se quiere distribuir
 * el tráfico entre múltiples instancias de un servicio.
 *
 * @author Ramos
 */
@Configuration
public class AppConfig {

    /**
     * Crea y configura un bean RestTemplate con soporte para balanceo de carga.
     *
     * @return instancia de RestTemplate con balanceo de carga habilitado.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
