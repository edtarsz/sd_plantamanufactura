package com.example.usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal del microservicio de usuarios.
 *
 * <p>Arranca la aplicación Spring Boot y habilita la funcionalidad de clientes Feign
 * para consumir otros microservicios.</p>
 * 
 * <p>Este es el punto de entrada del sistema.</p>
 * 
 * @author Ramos
 */
@EnableFeignClients
@SpringBootApplication
public class UsuarioApplication {

    /**
     * Método principal que ejecuta la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(UsuarioApplication.class, args);
    }
}
