/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.gateway.filter;

import com.example.gateway.util.JWTServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Ramos
 */
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

//    @Autowired
//    private RestTemplate restTemplate;
    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private JWTServicio jwtUtil;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(AUTHORIZATION)) {
                    throw new RuntimeException("No cuenta con la autorización en el header.");
                }

                String authHeader = exchange.getRequest().getHeaders().get(AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    //REST CALL TO AUTH
                    // restTemplate.getForObject("http://localhost/api/v1/auth/validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);

                } catch (RestClientException e) {
                    System.out.println("Acceso inválido");
                    throw new RuntimeException("Unauthorized acces to Application.");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
