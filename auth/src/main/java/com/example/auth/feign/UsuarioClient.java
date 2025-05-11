/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.auth.feign;

import com.example.auth.dto.UsuarioRequest;
import com.example.auth.dto.UsuarioResponse;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author Ramos
 */
@FeignClient(name = "usuarios")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/username/{username}")
    Optional<UsuarioResponse> findByUsername(@PathVariable String username);

    @PostMapping("/api/v1/usuarios")
    void createUsuario(@RequestBody UsuarioRequest usuarioRequest);
}
