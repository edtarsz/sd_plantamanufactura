package com.example.servicio_conversion_moneda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Clase principal que lanza la aplicación de conversión de moneda.
 *
 * <p>Utiliza Spring Boot para arrancar el contexto y exponer los endpoints REST.</p>
 * 
 * <p>Punto de entrada del sistema.</p>
 * 
 * @author PC
 */
@SpringBootApplication
public class ServicioConversionMonedaApplication {

    /**
     * Método <code>main</code> que arranca la aplicación Spring Boot.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(ServicioConversionMonedaApplication.class, args);
    }
}
