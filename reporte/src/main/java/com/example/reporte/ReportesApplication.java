/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Clase principal de la aplicación Spring Boot.
 *
 * Esta clase es el punto de entrada para iniciar la aplicación,
 * configurando y arrancando el contexto de Spring.
 *
 * Anotaciones:
 * - @SpringBootApplication: Marca esta clase como la configuración principal
 *   y habilita la configuración automática, escaneo de componentes y otras funcionalidades.
 * - @EnableJpaRepositories: Habilita el soporte para repositorios JPA,
 *   permitiendo que Spring detecte y cree implementaciones de interfaces JpaRepository.
 *
 * Contiene el método main que ejecuta la aplicación.
 *
 * @author Ramos
 */
@SpringBootApplication
@EnableJpaRepositories
public class ReportesApplication {

    /**
     * Método principal que arranca la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comando (no usados en este caso)
     */
    public static void main(String[] args) {
        SpringApplication.run(ReportesApplication.class, args);
    }
}
