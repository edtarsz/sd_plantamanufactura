/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa la respuesta extendida de un usuario, incluyendo sus reportes.
 *
 * <p>Se utiliza para retornar información compuesta del usuario y su historial
 * de reportes asociados.</p>
 *
 * <p>Contiene el nombre completo, nombre de usuario y una lista de reportes.</p>
 * 
 * <p>Utiliza anotaciones de Lombok para generar constructores, getters, setters y builder.</p>
 * 
 * @author Ramos
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullUsuarioResponse {

    private String fullName;
    private String username;

    /**
     * Lista de reportes asociados al usuario.
     */
    private List<Reporte> reportes;
}
