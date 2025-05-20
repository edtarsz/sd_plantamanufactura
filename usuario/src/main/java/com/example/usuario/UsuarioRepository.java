/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 *
 * <p>Extiende {@link JpaRepository} y proporciona métodos CRUD para la tabla <code>usuario</code>,
 * incluyendo consultas personalizadas como buscar por nombre de usuario.</p>
 *
 * @author Ramos
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario
     * @return un {@link Optional} que contiene al usuario si existe
     */
    Optional<Usuario> findByUsername(String username);
}

