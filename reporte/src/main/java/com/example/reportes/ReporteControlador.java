/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reportes;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ramos
 */
@RestController
@RequestMapping(path = "/api/v1/reportes")
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    public ReporteControlador(ReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    @GetMapping
    public List<Reporte> getAll() {
        return reporteServicio.getReportes();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reporte>> getAllReportes(@PathVariable("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(reporteServicio.findAllReportesByUsuario(idUsuario));
    }

    @GetMapping("/{idReporte}")
    public Optional<Reporte> getByID(@PathVariable("idReporte") Long idReporte) {
        return reporteServicio.getReporte(idReporte);
    }

    @PostMapping
    public void saveOrUpdate(@RequestBody Reporte reporte) {
        reporteServicio.saveOrUpdate(reporte);
    }

    @DeleteMapping("/{idReporte}")
    public void delete(@PathVariable("idReporte") Long idReporte) {
        reporteServicio.delete(idReporte);
    }
}
