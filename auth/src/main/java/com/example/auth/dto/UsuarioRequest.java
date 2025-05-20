package com.example.auth.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la información necesaria
 * para registrar un nuevo usuario.
 * 
 * Contiene los campos:
 * - fullName: nombre completo del usuario.
 * - username: nombre de usuario único para login.
 * - password: contraseña en texto plano (antes de ser codificada).
 * 
 * La anotación Lombok {@code @Data} genera automáticamente
 * los métodos getters, setters, toString, equals y hashCode.
 * 
 * @author Ramos
 */
@Data
public class UsuarioRequest {

    private String fullName;
    private String username;
    private String password;
}
