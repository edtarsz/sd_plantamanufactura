/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.config;

import com.example.auth.dto.UsuarioResponse;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Ramos
 */
public class DetallesUsuario implements UserDetails {

    private String username;
    private String password;

    public DetallesUsuario(UsuarioResponse usuarioResponse, PasswordEncoder encoder) {
        this.username = usuarioResponse.getUsername();
        this.password = usuarioResponse.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
