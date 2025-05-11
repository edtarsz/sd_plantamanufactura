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

/**
 *
 * @author Ramos
 */
public class DetallesUsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioClient usuarioClient;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UsuarioResponse> usuario = usuarioClient.findByUsername(username);
        return usuario.map(DetallesUsuario::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
