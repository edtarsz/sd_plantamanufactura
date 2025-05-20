/*
 * Configuración de beans para la aplicación Gateway.
 *
 * Esta clase define un bean de tipo RestTemplate que puede ser utilizado
 * para realizar llamadas HTTP sincronas hacia otros servicios.
 * 
 * No está configurado con balanceo de carga (LoadBalanced).
 * 
 * @author Ramos
 */
package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate template() {
        return new RestTemplate();
    }
}
