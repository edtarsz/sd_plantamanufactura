/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.controlador;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.UsuarioRequest;
import com.example.auth.feign.UsuarioClient;
import com.example.auth.servicios.AuthServicio;
import com.example.auth.servicios.JWTServicio;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    private JWTServicio jwtServicio;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> addNewUser(@RequestBody UsuarioRequest usuarioRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            String encodedPassword = new BCryptPasswordEncoder().encode(usuarioRequest.getPassword());
            usuarioRequest.setPassword(encodedPassword);
            usuarioClient.createUsuario(usuarioRequest);

            response.put("message", "Usuario registrado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al registrar: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );

            String token = jwtServicio.generateToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        servicioAuth.validateToken(token);
        return "La llave es válida";
    }
}
