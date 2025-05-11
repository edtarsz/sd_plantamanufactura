/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.config;

import com.example.auth.entidades.Usuario;
import com.example.auth.repositorios.UsuarioRepository;
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
    private UsuarioRepository usuarioRepositorio;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepositorio.findByUsername(username);
        return usuario.map(DetallesUsuario::new).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre: " + username));
    }
    
}
