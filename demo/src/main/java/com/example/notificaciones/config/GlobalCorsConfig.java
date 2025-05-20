/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.notificaciones.config;

/**
 *
 * @author crist
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing) para la aplicación.
 * 
 * <p>Esta clase implementa la interfaz {@link WebMvcConfigurer} para registrar
 * una política de CORS que permita solicitudes desde orígenes específicos,
 * habilitando el acceso entre distintos dominios cuando se trabaja con microservicios,
 * frontends locales o proxies como Spring Cloud Gateway.</p>
 * 
 * <p>Los orígenes permitidos en esta configuración son:
 * <ul>
 *   <li><code>http://127.0.0.1:5500</code></li>
 *   <li><code>http://localhost:5500</code></li>
 *   <li><code>http://localhost:8222</code></li>
 * </ul>
 * 
 * <p>Además, se permite cualquier método HTTP, cualquier cabecera y el envío de credenciales como cookies o cabeceras de autenticación.</p>
 * 
 * @author crist
 */
@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    /**
     * Configura los mapeos CORS globales para todos los endpoints de la aplicación.
     * 
     * <p>Este método especifica qué orígenes, métodos y cabeceras están permitidos
     * para realizar peticiones a la API, además de habilitar el uso de credenciales.</p>
     *
     * @param registry el registro de configuraciones CORS de Spring MVC
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8222")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}

