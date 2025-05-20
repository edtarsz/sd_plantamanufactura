
package com.example.gateway.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/*
 * Validador de rutas para determinar si un endpoint requiere autenticación.
 * 
 * Esta clase contiene una lista de rutas públicas (sin necesidad de autenticación)
 * y un predicado que verifica si una solicitud HTTP debe ser considerada segura,
 * es decir, que requiere validación de token o acceso restringido.
 * 
 * Utiliza AntPathMatcher para permitir patrones con comodines en las rutas.
 * 
 * @author Ramos
 */
@Component
public class RouteValidator {

    /**
     * Lista de endpoints abiertos que no requieren autenticación. Incluye rutas
     * de autenticación, registro y recursos estáticos.
     */
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/token",
            "/login",
            "/signup",
            "/css/**",
            "/js/**",
            "/img/**"
    );

    /**
     * Predicado que determina si una petición HTTP debe considerarse segura, es
     * decir, que no corresponde a una ruta pública y por lo tanto requiere
     * autenticación.
     */
    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        return openApiEndpoints.stream()
                .noneMatch(pattern -> new AntPathMatcher().match(pattern, path));
    };

}
