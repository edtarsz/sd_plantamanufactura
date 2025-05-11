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
 *
 * @author Ramos
 */
@FeignClient(name = "reportes")
public interface ReporteClient {

    @GetMapping("/usuario/{idUsuario}")
    List<Reporte> findAllReportesByUsuario(@PathVariable("idUsuario") Long idUsuario);

}
