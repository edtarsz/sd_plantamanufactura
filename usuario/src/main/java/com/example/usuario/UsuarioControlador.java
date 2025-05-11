/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ramos
 */
@RestController
@RequestMapping(path = "/api/v1/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    @Autowired
    UsuarioRepository usuarioRepositorio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // GET:: /api/v1/usuarios
    @GetMapping
    public List<Usuario> getAll() {
        return usuarioServicio.getUsuarios();
    }

    // GET:: /api/v1/usuarios/{idUsuario}
    @GetMapping("/{idUsuario}")
    public Optional<Usuario> getByID(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioServicio.getUsuario(idUsuario);
    }

    @PostMapping
    public void saveOrUpdate(@RequestBody Usuario usuario) {
        usuarioServicio.saveOrUpdate(usuario);
    }

    @DeleteMapping("/{idUsuario}")
    public void delete(@PathVariable("idUsuario") Long idUsuario) {
        usuarioServicio.delete(idUsuario);
    }

    // GET:: /api/v1/usuarios/con-reportes/{idUsuario}
    @GetMapping("/con-reportes/{idUsuario}")
    public FullUsuarioResponse getAll(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioServicio.findAllReportesByUsuario(idUsuario);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponse> getByUsername(@PathVariable String username) {
        Optional<Usuario> usuario = usuarioRepositorio.findByUsername(username);
        return usuario.map(u -> new UsuarioResponse(u.getUsername(), u.getPassword()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
