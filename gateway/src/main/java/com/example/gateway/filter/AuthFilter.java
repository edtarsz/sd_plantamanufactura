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

        if (!routeValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        // Obtener el token del header o de la cookie
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            // Buscar el token en las cookies
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

        if (token == null || !token.startsWith("Bearer ")) {
            return redirectToLogin(exchange);
        }

        try {
            String jwt = token.substring(7);
            jwtService.validateToken(jwt);
            return chain.filter(exchange);
        } catch (Exception e) {
            return redirectToLogin(exchange);
        }
    }

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
