/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author Ramos
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(auth
                        -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/img/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form
                        -> form
                        .loginPage("/redirect/login") // Tu página personalizada
                        .loginProcessingUrl("/redirect/login") // URL que procesa el formulario
                        .defaultSuccessUrl("/redirect/index", true) // Redirigir tras login exitoso
                        .failureUrl("/redirect/login?error=true") // Redirigir si falla
                        .permitAll()
                ).logout(logout
                        -> logout
                        .logoutSuccessUrl("/redirect/login?logout") // Redirigir tras logout
                        .permitAll()
                );

        return http.build();
    }
}
