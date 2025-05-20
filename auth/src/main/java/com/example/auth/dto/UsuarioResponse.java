package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) que representa la respuesta con la información
 * del usuario obtenida desde el servicio o base de datos.
 * 
 * Contiene los campos:
 * - idUsuario: identificador único del usuario.
 * - username: nombre de usuario.
 * - password: contraseña codificada.
 * 
 * Las anotaciones Lombok usadas:
 * - @Getter y @Setter para los métodos de acceso.
 * - @NoArgsConstructor para constructor sin argumentos.
 * - @AllArgsConstructor para constructor con todos los campos.
 * 
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long idUsuario;
    private String username;
    private String password;
}
