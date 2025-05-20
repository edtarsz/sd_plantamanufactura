
package com.example.gateway.filter;

import com.example.gateway.util.JWTServicio;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;

/*
 * Filtro global para la autenticación en el API Gateway.
 * 
 * Este filtro intercepta todas las solicitudes entrantes y valida si la ruta
 * requiere autenticación. Si es así, intenta extraer y validar un token JWT
 * presente en el header Authorization o en una cookie llamada "authToken".
 * 
 * Si la validación falla o no se encuentra token válido, redirige al login
 * o responde con UNAUTHORIZED según el tipo de petición.
 * 
 * Usa RouteValidator para determinar qué rutas son públicas y cuáles protegidas.
 * 
 * @author Ramos
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JWTServicio jwtService;

    @Autowired
    private RouteValidator routeValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Si la ruta es pública, continuar sin autenticación
        if (!routeValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        // Obtener token del header Authorization o de cookies
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            String cookieHeader = request.getHeaders().getFirst("Cookie");
            if (cookieHeader != null) {
                String[] cookies = cookieHeader.split("; ");
                for (String cookie : cookies) {
                    if (cookie.startsWith("authToken=")) {
                        token = "Bearer " + cookie.substring("authToken=".length());
                        break;
                    }
                }
            }
        }

        // Si no hay token válido, redirigir o denegar acceso
        if (token == null || !token.startsWith("Bearer ")) {
            return redirectToLogin(exchange);
        }

        try {
            // Validar token JWT
            String jwt = token.substring(7);
            jwtService.validateToken(jwt);
            return chain.filter(exchange);
        } catch (Exception e) {
            // Token inválido o expirado: redirigir o denegar
            return redirectToLogin(exchange);
        }
    }

    /**
     * Redirige a la página de login o responde con código 401 si la petición es
     * API.
     *
     * @param exchange Contexto de la petición HTTP
     * @return Mono<Void> para continuar con la respuesta HTTP
     */
    private Mono<Void> redirectToLogin(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().toString();

        if (path.startsWith("/api")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
            exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
        }

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
