package com.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración de seguridad para la aplicación de autenticación.
 * 
 * Define beans esenciales para la autenticación y autorización de usuarios
 * usando Spring Security.
 * 
 * Anotaciones usadas:
 * - @Configuration: Marca la clase como una clase de configuración para Spring.
 * - @EnableWebSecurity: Habilita la configuración de seguridad web de Spring Security.
 * 
 * Esta clase:
 * - Define el servicio para cargar los detalles de usuario.
 * - Configura la cadena de filtros de seguridad (SecurityFilterChain).
 * - Configura el codificador de contraseñas.
 * - Define el proveedor de autenticación.
 * - Expone el administrador de autenticación.
 * 
 */
@Configuration
@EnableWebSecurity
public class AuthConfig {

    /**
     * Bean que provee la implementación de UserDetailsService.
     * Esta clase es responsable de cargar los detalles del usuario (usuario, contraseña, roles).
     * 
     * @return instancia de UserDetailsService personalizada.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new DetallesUsuarioServicio();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * 
     * - Deshabilita CSRF (Cross-Site Request Forgery).
     * - Permite el acceso sin autenticación a las rutas:
     *   "/api/v1/auth/register", "/api/v1/auth/token" y "/api/v1/auth/validate".
     * - Requiere autenticación para cualquier otra solicitud.
     * 
     * @param httpSecurity configuración HTTP a modificar.
     * @return objeto SecurityFilterChain configurado.
     * @throws Exception si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/token",
                                "/api/v1/auth/validate"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    /**
     * Bean que proporciona el codificador de contraseñas.
     * Utiliza BCrypt para hashing seguro de las contraseñas.
     * 
     * @return instancia de PasswordEncoder con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el AuthenticationProvider que valida la autenticación del usuario.
     * Utiliza un DaoAuthenticationProvider que delega en el UserDetailsService y
     * el PasswordEncoder para verificar las credenciales.
     * 
     * @return instancia configurada de AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposición del AuthenticationManager para gestionar la autenticación.
     * 
     * @param config configuración de autenticación proporcionada por Spring.
     * @return instancia del AuthenticationManager.
     * @throws Exception si no se puede obtener el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
