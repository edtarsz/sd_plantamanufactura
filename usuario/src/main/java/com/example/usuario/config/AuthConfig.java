/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el microservicio de usuarios.
 *
 * <p>Define una cadena de filtros de seguridad personalizada y un codificador de contraseñas.</p>
 *
 * <p>Actualmente, se permite el acceso sin autenticación a todos los endpoints.</p>
 * 
 * @author Ramos
 */
@Configuration
@EnableWebSecurity
public class AuthConfig {

    /**
     * Define la cadena de filtros de seguridad.
     *
     * <p>En esta configuración, se desactiva CSRF y se permite cualquier petición.</p>
     *
     * @param http configuración HTTP de Spring Security
     * @return la cadena de filtros
     * @throws Exception en caso de error de configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
                );
        return http.build();
    }

    /**
     * Proporciona un codificador de contraseñas basado en BCrypt.
     *
     * @return codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
