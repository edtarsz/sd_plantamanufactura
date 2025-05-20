package com.example.servicio_conversion_moneda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
/**
 * Clase de configuración general de la aplicación.
 *
 * <p>Define beans compartidos como {@link RestTemplate} para consumo de servicios REST externos.</p>
 * 
 * <p>Permite inyección automática de dependencias mediante Spring.</p>
 * 
 * @author PC
 */
@Configuration
public class AppConfig {

    /**
     * Bean que expone un {@link RestTemplate} para realizar llamadas HTTP REST.
     *
     * @return instancia de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
