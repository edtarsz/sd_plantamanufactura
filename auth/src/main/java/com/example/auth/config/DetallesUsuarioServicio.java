/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.config;

import com.example.auth.dto.UsuarioResponse;
import com.example.auth.feign.UsuarioClient;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
public class DetallesUsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UsuarioResponse> usuario = usuarioClient.findByUsername(username);
        return usuario.map(u -> new DetallesUsuario(u, passwordEncoder))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
