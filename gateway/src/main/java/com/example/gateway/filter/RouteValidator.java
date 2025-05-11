/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.gateway.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ramos
 */
@Component
public class RouteValidator {

    // TO DO, Solo debería limitar el acceso solo a los métodos necesarios
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/token",
            "/login",
            "/signup",
            "/css/**",
            "/js/**",
            "/img/**"
    );

    public Predicate<ServerHttpRequest> isSecured
            = request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
