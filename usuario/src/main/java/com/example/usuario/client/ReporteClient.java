/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.usuario.client;

import com.example.usuario.Reporte;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicarse con el microservicio de reportes.
 *
 * <p>Permite obtener todos los reportes asociados a un usuario dado su ID.</p>
 *
 * <p>El nombre del microservicio al que se conecta es <code>reportes</code>.</p>
 * 
 * @author Ramos
 */
@FeignClient(name = "reportes")
public interface ReporteClient {

    /**
     * Realiza una petición GET al microservicio de reportes para obtener los reportes de un usuario.
     *
     * @param idUsuario ID del usuario
     * @return lista de reportes asociados
     */
    @GetMapping("/usuario/{idUsuario}")
    List<Reporte> findAllReportesByUsuario(@PathVariable("idUsuario") Long idUsuario);
}
