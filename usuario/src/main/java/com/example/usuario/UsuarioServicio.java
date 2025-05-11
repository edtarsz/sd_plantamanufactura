/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import com.example.client.ReporteClient;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class UsuarioServicio {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ReporteClient reporteClient;

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    public void saveOrUpdate(Usuario usuario) {
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
}
