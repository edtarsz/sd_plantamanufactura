/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.servicios;

import com.example.auth.dto.UsuarioResponse;
import com.example.auth.feign.UsuarioClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Servicio encargado de la generación y validación de tokens JWT. Este servicio
 * se utiliza para autenticar usuarios y asegurar la comunicación.
 *
 * Utiliza una clave secreta compartida para firmar y verificar los tokens.
 *
 * El token generado incluye el nombre de usuario como "subject" y el idUsuario
 * como un claim personalizado.
 *
 * @author Ramos
 */
@Component
public class JWTServicio {

    // Clave secreta en Base64 utilizada para firmar/verificar los JWTs (debe mantenerse segura)
    private static final String SECRET = "A213SD331213313332132DA213SD331213313332132D";

    // Cliente Feign que permite comunicarse con el microservicio de usuarios
    @Autowired
    private UsuarioClient usuarioClient;

    /**
     * Valida un token JWT asegurando que esté correctamente firmado y que no
     * haya expirado.
     *
     * @param token El token JWT a validar.
     * @return Un objeto Jws<Claims> que contiene los claims del token si es
     * válido.
     */
    public Jws<Claims> validateToken(final String token) {
        return Jwts.parser() // Crea un parser para analizar el JWT
                .verifyWith(getSignKey()) // Verifica la firma con la clave secreta
                .build()
                .parseSignedClaims(token); // Parsea el token y extrae los claims firmados
    }

    /**
     * Genera un token JWT con el nombre de usuario como sujeto y el ID del
     * usuario como claim. El token tiene una duración de 30 minutos.
     *
     * @param username El nombre de usuario del cual se quiere generar el token.
     * @return Un token JWT firmado.
     */
    public String generateToken(String username) {
        // Obtener usuario desde el microservicio de usuarios
        UsuarioResponse usuario = usuarioClient.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getIdUsuario()); // userId como Long

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Fecha de expiración (30 minutos)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Obtiene el ID del usuario consultando al microservicio de usuarios
     * mediante Feign.
     *
     * @param username El nombre de usuario.
     * @return El ID del usuario asociado al nombre de usuario.
     * @throws RuntimeException si el usuario no es encontrado.
     */
    private Long obtenerUserIdPorUsername(String username) {
        UsuarioResponse response = usuarioClient.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return response.getIdUsuario();
    }

    /**
     * Convierte la clave secreta codificada en Base64 a un objeto SecretKey
     * para firmar/verificar los JWT.
     *
     * @return Clave secreta como objeto SecretKey.
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
