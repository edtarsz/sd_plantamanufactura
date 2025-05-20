package com.example.auth.feign;

import com.example.auth.dto.UsuarioRequest;
import com.example.auth.dto.UsuarioResponse;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente Feign para comunicarse con el servicio externo de usuarios.
 *
 * Define los endpoints remotos que pueden ser invocados para obtener o crear
 * usuarios.
 *
 * - findByUsername: busca un usuario por su nombre de usuario. - createUsuario:
 * registra un nuevo usuario enviando la información requerida.
 *
 * El nombre del cliente se configura con
 * {@code @FeignClient(name = "usuarios")}.
 *
 * @author Ramos
 */
@FeignClient(name = "usuarios")
public interface UsuarioClient {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario para la búsqueda.
     * @return Un {@link Optional} con {@link UsuarioResponse} si el usuario
     * existe, vacío si no.
     */
    @GetMapping("/api/v1/usuarios/username/{username}")
    Optional<UsuarioResponse> findByUsername(@PathVariable String username);

    /**
     * Crea un nuevo usuario enviando un objeto {@link UsuarioRequest}.
     *
     * @param usuarioRequest Datos del usuario a crear.
     * @return Una {@link ResponseEntity} que contiene la respuesta HTTP del
     * servicio.
     */
    @PostMapping("/api/v1/usuarios")
    ResponseEntity createUsuario(@RequestBody UsuarioRequest usuarioRequest);
}
