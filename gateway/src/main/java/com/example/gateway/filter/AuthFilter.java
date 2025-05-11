/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
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

        // Si la ruta es pública, permite el acceso
        if (!routeValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        // Extrae el token del header "Authorization"
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return redirectToLogin(exchange); // Redirige si no hay token
        }

        try {
            // Valida el token JWT
            String jwt = token.substring(7);
            jwtService.validateToken(jwt);
            return chain.filter(exchange);
        } catch (Exception e) {
            return redirectToLogin(exchange);
        }
    }

    private Mono<Void> redirectToLogin(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().toString();

        // Para APIs devuelve 401 (sin redirección)
        if (path.startsWith("/api")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        } // Para páginas web redirige a /login
        else {
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
