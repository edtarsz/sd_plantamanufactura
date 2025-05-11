/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.servicios;

import com.example.auth.entidades.Usuario;
import com.example.auth.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
public class AuthServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTServicio jwtServicio;

    public String saveUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "Usuario guardado en el sistema";
    }

    public String generateToken(String username) {
        return jwtServicio.generateToken(username);
    }

    public void validateToken(String token) {
        jwtServicio.validateToken(token);
    }
}
