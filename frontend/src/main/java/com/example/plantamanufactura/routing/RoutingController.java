/*
 * Controlador encargado de manejar las rutas de navegación en la aplicación frontend.
 * 
 * Cada método maneja una ruta GET y devuelve el nombre de la vista correspondiente,
 * que será resuelta por el motor de plantillas (por ejemplo, Thymeleaf).
 * 
 * @author Ramos
 */
package com.example.plantamanufactura.routing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoutingController {

    /**
     * Muestra la página para generar reportes.
     *
     * @return nombre de la vista "generarReportes"
     */
    @GetMapping("/generarReportes")
    public String generarReportes() {
        return "generarReportes";
    }

    /**
     * Muestra el catálogo de defectos.
     *
     * @return nombre de la vista "catalogoDefectos"
     */
    @GetMapping("/catalogoDefectos")
    public String catalogoDefectos() {
        return "catalogoDefectos";
    }

    /**
     * Muestra la página para el registro de defectos.
     *
     * @return nombre de la vista "registroDefectos"
     */
    @GetMapping("/registroDefectos")
    public String registroDefectos() {
        return "registroDefectos";
    }

    /**
     * Muestra la página de login.
     *
     * @return nombre de la vista "login"
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Muestra la página de registro de usuario (signup).
     *
     * @return nombre de la vista "signup"
     */
    @GetMapping("/signup")
    public String signUp() {
        return "signup";
    }
}
