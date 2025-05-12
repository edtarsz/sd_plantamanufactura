/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.routing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Ramos
 */
@Controller
public class RoutingController {

    @GetMapping("/generarReportes")
    public String generarReportes() {
        return "generarReportes";
    }

    @GetMapping("/catalogoDefectos")
    public String catalogoDefectos() {
        return "catalogoDefectos";
    }

    @GetMapping("/registroDefectos")
    public String registroDefectos() {
        return "registroDefectos";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signup";
    }
}
