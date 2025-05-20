/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO simple que representa una respuesta básica de usuario.
 *
 * <p>Se utiliza para exponer solo ciertos campos del usuario, como su ID,
 * nombre de usuario y contraseña (aunque incluir contraseñas no es recomendado en producción).</p>
 *
 * <p>Ideal para autenticación o búsqueda específica por username.</p>
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
