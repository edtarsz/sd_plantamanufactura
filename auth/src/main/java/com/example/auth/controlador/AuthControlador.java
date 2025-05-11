/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.controlador;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.UsuarioRequest;
import com.example.auth.feign.UsuarioClient;
import com.example.auth.servicios.AuthServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ramos
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthControlador {

    @Autowired
    private AuthServicio servicioAuth;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioClient usuarioClient;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UsuarioRequest usuarioRequest) {
        // Encripta la contraseña antes de enviarla al MS de usuario
        String encodedPassword = new BCryptPasswordEncoder().encode(usuarioRequest.getPassword());
        usuarioRequest.setPassword(encodedPassword);

        usuarioClient.createUsuario(usuarioRequest);
        return "Usuario registrado exitosamente";
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()) {
            return servicioAuth.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("Usuario inválido");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        servicioAuth.validateToken(token);
        return "La llave es válida";
    }
}
