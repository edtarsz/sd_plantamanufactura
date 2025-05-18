/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import com.example.reporte.defectos.DefectoDTO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/detalle/{idReporte}")
    public ResponseEntity<Reporte> getDetalleReporte(@PathVariable Long idReporte) {
        return reporteServicio.getReporte(idReporte)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private String getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" ->
                "application/pdf";
            case "excel" ->
                "application/vnd.ms-excel";
            default ->
                "application/octet-stream";
        };
    }

    @GetMapping("/{idReporte}")
    public Optional<Reporte> getByID(@PathVariable("idReporte") Long idReporte) {
        return reporteServicio.getReporte(idReporte);
    }

    @PostMapping
    public ResponseEntity<Reporte> saveOrUpdate(@RequestBody Reporte reporte) {
        Reporte savedReporte = reporteServicio.saveOrUpdate(reporte);
        return ResponseEntity.ok(savedReporte);
    }

    @DeleteMapping("/{idReporte}")
    public void delete(@PathVariable("idReporte") Long idReporte) {
        reporteServicio.delete(idReporte);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<Reporte>> filtrarReportes(
            @RequestBody Map<String, String> filtros,
            @RequestParam Long idUsuario) {

        List<Reporte> reportesFiltrados = reporteServicio.filtrarReportes(filtros, idUsuario);
        return ResponseEntity.ok(reportesFiltrados);
    }

    @GetMapping("/exportar")
    public void exportarReportes(
            @RequestParam Map<String, String> params,
            HttpServletResponse response) throws IOException {

        String format = params.getOrDefault("format", "pdf");
        String currency = params.getOrDefault("currency", "USD");

        response.setContentType(getContentType(format));
        response.setHeader("Content-Disposition", "attachment; filename=reporte." + format);

        reporteServicio.exportarReportes(params, currency, response.getOutputStream());
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<ReporteDTO>> getReportesPorUsuario(@PathVariable Long userId) {
        List<Reporte> reportes = reporteServicio.getReportesConDetalles(userId);

        List<ReporteDTO> dtos = reportes.stream().map(reporte -> {
            ReporteDTO dto = new ReporteDTO();
            dto.setIdReporte(reporte.getIdReporte());
            dto.setLoteId(reporte.getLoteId());
            dto.setCostoTotal(reporte.getCostoTotal());
            dto.setInspector(reporte.getInspector());
            dto.setDefectos(reporte.getDefectos().stream().map(defecto -> {
                DefectoDTO defectoDto = new DefectoDTO();
                defectoDto.setTipoDefecto(defecto.getTipoDefecto().getNombre());
                defectoDto.setCantidad_piezas(defecto.getCantidad_piezas());
                defectoDto.setCosto(defecto.getCosto());
                defectoDto.setDetalles(defecto.getDetalles());
                return defectoDto;
            }).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
