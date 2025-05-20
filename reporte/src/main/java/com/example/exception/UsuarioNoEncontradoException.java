/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;

/*
 * Excepción personalizada que se lanza cuando no se encuentra un usuario
 * con el identificador proporcionado en el sistema.
 *
 * Hereda de RuntimeException para indicar un error en tiempo de ejecución
 * relacionado con la ausencia de un recurso esperado.
 * 
 * @author Ramos
 */
public class UsuarioNoEncontradoException extends RuntimeException {

    /**
     * Constructor que recibe el ID del usuario no encontrado y construye un
     * mensaje descriptivo para la excepción.
     *
     * @param idUsuario Identificador del usuario buscado sin resultados
     */
    public UsuarioNoEncontradoException(Long idUsuario) {
        super("Usuario no encontrado con ID: " + idUsuario);
    }
}
