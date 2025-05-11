/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.auth.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
public class AuthServicio {

    @Autowired
    private JWTServicio jwtServicio;

    public String generateToken(String username) {
        return jwtServicio.generateToken(username);
    }

    public void validateToken(String token) {
        jwtServicio.validateToken(token);
    }
}
