/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * DTO (Data Transfer Object) que representa la información básica de un usuario.
 *
 * Esta clase se utiliza para transportar datos relacionados con el usuario
 * entre diferentes capas de la aplicación, sin exponer la entidad completa.
 *
 * Contiene campos comunes como identificador, nombre completo y nombre de usuario.
 *
 * Se utilizan anotaciones de Lombok para generar automáticamente
 * los métodos getters, setters, constructores sin argumentos y con todos los argumentos.
 * 
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    /**
     * Identificador único del usuario.
     */
    private Long idUsuario;

    /**
     * Nombre completo del usuario.
     */
    private String fullName;

    /**
     * Nombre de usuario para login o identificación.
     */
    private String username;
}
