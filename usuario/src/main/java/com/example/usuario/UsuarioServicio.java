/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import com.example.usuario.client.ReporteClient;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de negocio para gestionar operaciones sobre usuarios.
 *
 * <p>Realiza operaciones CRUD y también integra con otros microservicios como el de reportes,
 * utilizando {@link ReporteClient} para obtener datos relacionados.</p>
 * 
 * <p>Utiliza inyección de dependencias a través de constructor gracias a {@link AllArgsConstructor}.</p>
 * 
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepository usuarioRepository;
    private final ReporteClient reporteClient;

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por ID.
     *
     * @param id identificador del usuario
     * @return usuario si existe
     */
    public Optional<Usuario> getUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Guarda o actualiza un usuario.
     *
     * @param usuario objeto a persistir
     */
    public void saveOrUpdate(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario por ID.
     *
     * @param id identificador del usuario
     */
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Obtiene un usuario junto con todos sus reportes asociados.
     *
     * @param idUsuario identificador del usuario
     * @return respuesta completa con usuario y reportes
     */
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

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return usuario si existe
     */
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
