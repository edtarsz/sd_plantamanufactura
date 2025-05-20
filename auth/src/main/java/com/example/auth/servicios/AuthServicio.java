package com.example.auth.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación que actúa como intermediario para la generación y
 * validación de tokens JWT.
 *
 * Delegación hacia el servicio {@link JWTServicio} para las operaciones de
 * generación y validación de tokens.
 *
 * @author Ramos
 */
@Service
public class AuthServicio {

    @Autowired
    private JWTServicio jwtServicio;

    /**
     * Genera un token JWT para un usuario dado.
     *
     * @param username Nombre de usuario para quien se genera el token.
     * @return Token JWT generado como String.
     */
    public String generateToken(String username) {
        return jwtServicio.generateToken(username);
    }

    /**
     * Valida la validez y autenticidad de un token JWT.
     *
     * @param token Token JWT a validar.
     */
    public void validateToken(String token) {
        jwtServicio.validateToken(token);
    }
}
