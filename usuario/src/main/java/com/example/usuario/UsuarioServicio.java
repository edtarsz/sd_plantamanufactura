/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import com.example.usuario.client.ReporteClient;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepository usuarioRepository;
    private final ReporteClient reporteClient;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    public void saveOrUpdate(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    public FullUsuarioResponse findAllReportesByUsuario(Long idUsuario) {
        var usuario = usuarioRepository.findById(idUsuario).orElse(
                Usuario.builder()
                        .fullName("NOT_FOUND")
                        .username("NOT_FOUND")
                        .build());
        var reportes = reporteClient.findAllReportesByUsuario(idUsuario);

        return FullUsuarioResponse
                .builder()
                .fullName(usuario.getFullName())
                .username(usuario.getUsername())
                .reportes(reportes)
                .build();
    }

    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
