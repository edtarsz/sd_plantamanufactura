/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.routing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Ramos
 */
@Controller
public class RoutingController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Redirige al login con posibles parámetros de error/logout
     *
     * @param error
     * @param logout
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Sesión cerrada");
        }
        return "login";
    }
}
