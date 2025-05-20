package com.example.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/*
 * Servicio para la validación de tokens JWT.
 * 
 * Esta clase se encarga de verificar y validar tokens JWT utilizando una clave secreta,
 * asegurando que los tokens sean legítimos y no hayan sido alterados.
 * 
 * Utiliza la librería jjwt para el procesamiento del JWT.
 * 
 * @author Ramos
 */
@Component
public class JWTServicio {

    /**
     * Clave secreta codificada en Base64 que se usa para firmar y validar los
     * tokens JWT.
     */
    private static final String SECRET = "A213SD331213313332132DA213SD331213313332132D";

    /**
     * Valida el token JWT recibido y retorna sus claims si es válido.
     *
     * @param token Token JWT en formato String que se desea validar.
     * @return Un objeto Jws<Claims> que contiene las reclamaciones (claims) del
     * token.
     * @throws io.jsonwebtoken.JwtException si el token es inválido o está
     * corrupto.
     */
    public Jws<Claims> validateToken(final String token) {
        return Jwts
                .parser() // Crea el parser del JWT
                .verifyWith(getSignKey()) // Asocia la clave secreta para verificar la firma
                .build() // Construye el parser configurado
                .parseSignedClaims(token);       // Parsea y valida el token firmado
    }

    /**
     * Obtiene la clave secreta en formato SecretKey a partir de la cadena
     * codificada en Base64.
     *
     * @return SecretKey para la verificación de la firma del JWT.
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
