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
 * Controlador REST que gestiona las operaciones CRUD para los usuarios.
 *
 * <p>Expone endpoints para listar, consultar, guardar y eliminar usuarios.
 * También permite consultar usuarios junto con sus reportes o por su username.</p>
 *
 * <p>Base URL: <code>/api/v1/usuarios</code></p>
 * 
 * @author Ramos
 */
@RestController
@RequestMapping(path = "/api/v1/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    @Autowired
    UsuarioRepository usuarioRepositorio;

    /**
     * Constructor con inyección del servicio de usuario.
     *
     * @param usuarioServicio componente que gestiona la lógica del negocio
     */
    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @return lista de usuarios
     */
    @GetMapping
    public List<Usuario> getAll() {
        return usuarioServicio.getUsuarios();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param idUsuario identificador del usuario
     * @return objeto {@link Usuario} si existe, o vacío si no
     */
    @GetMapping("/{idUsuario}")
    public Optional<Usuario> getByID(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioServicio.getUsuario(idUsuario);
    }

    /**
     * Guarda o actualiza un usuario.
     *
     * @param usuario objeto {@link Usuario} a guardar
     */
    @PostMapping
    public void saveOrUpdate(@RequestBody Usuario usuario) {
        usuarioServicio.saveOrUpdate(usuario);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param idUsuario identificador del usuario a eliminar
     */
    @DeleteMapping("/{idUsuario}")
    public void delete(@PathVariable("idUsuario") Long idUsuario) {
        usuarioServicio.delete(idUsuario);
    }

    /**
     * Obtiene un usuario con su lista de reportes asociados.
     *
     * @param idUsuario identificador del usuario
     * @return objeto {@link FullUsuarioResponse}
     */
    @GetMapping("/con-reportes/{idUsuario}")
    public FullUsuarioResponse getAll(@PathVariable("idUsuario") Long idUsuario) {
        return usuarioServicio.findAllReportesByUsuario(idUsuario);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return objeto {@link UsuarioResponse} si se encuentra, o 404 si no
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponse> getByUsername(@PathVariable String username) {
        Optional<Usuario> usuario = usuarioRepositorio.findByUsername(username);
        return usuario.map(u -> new UsuarioResponse(
                u.getIdUsuario(),
                u.getUsername(),
                u.getPassword()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
