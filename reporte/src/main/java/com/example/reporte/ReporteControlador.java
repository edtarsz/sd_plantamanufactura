/*
 * Controlador REST para la gestión de reportes de defectos.
 * 
 * Proporciona endpoints para crear, consultar, actualizar, eliminar y exportar
 * reportes de defectos en diferentes formatos.
 * 
 * @author Ramos
 * @version 1.0
 */
package com.example.reporte;

import com.example.reporte.defectos.DefectoDTO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
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
 * Controlador REST que expone los endpoints relacionados con reportes de defectos.
 * 
 * Proporciona operaciones CRUD y funcionalidades adicionales como filtrado
 * y exportación de reportes en diversos formatos.
 */
@RestController
@RequestMapping(path = "/api/v1/reportes")
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    /**
     * Constructor para la inyección de dependencias.
     * 
     * @param reporteServicio Servicio que maneja la lógica de negocio de reportes
     */
    public ReporteControlador(ReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    /**
     * Endpoint para obtener el detalle completo de un reporte específico.
     * 
     * @param idReporte ID del reporte a consultar
     * @return ResponseEntity con el reporte si existe, o 404 si no se encuentra
     */
    @GetMapping("/detalle/{idReporte}")
    public ResponseEntity<Reporte> getDetalleReporte(@PathVariable Long idReporte) {
        return reporteServicio.getReporte(idReporte)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para obtener un reporte por su ID.
     * 
     * @param idReporte ID del reporte a consultar
     * @return Optional con el reporte si existe
     */
    @GetMapping("/{idReporte}")
    public Optional<Reporte> getByID(@PathVariable("idReporte") Long idReporte) {
        return reporteServicio.getReporte(idReporte);
    }

    /**
     * Endpoint para guardar o actualizar un reporte.
     * 
     * @param reporte Objeto reporte a guardar o actualizar
     * @return ResponseEntity con el reporte guardado
     */
    @PostMapping
    public ResponseEntity<Reporte> saveOrUpdate(@RequestBody Reporte reporte) {
        Reporte savedReporte = reporteServicio.saveOrUpdate(reporte);
        return ResponseEntity.ok(savedReporte);
    }

    /**
     * Endpoint para eliminar un reporte por su ID.
     * 
     * @param idReporte ID del reporte a eliminar
     */
    @DeleteMapping("/{idReporte}")
    public void delete(@PathVariable("idReporte") Long idReporte) {
        reporteServicio.delete(idReporte);
    }

    /**
     * Endpoint para filtrar reportes según criterios específicos.
     * 
     * @param filtros Mapa de criterios de filtrado
     * @param idUsuario ID del usuario para filtrar reportes
     * @return ResponseEntity con la lista de reportes filtrados
     */
    @PostMapping("/filtrar")
    public ResponseEntity<List<Reporte>> filtrarReportes(
            @RequestBody Map<String, String> filtros,
            @RequestParam Long idUsuario) {

        List<Reporte> reportesFiltrados = reporteServicio.filtrarReportes(filtros, idUsuario);
        return ResponseEntity.ok(reportesFiltrados);
    }

    /**
     * Endpoint para exportar reportes en diferentes formatos.
     * 
     * Permite exportar reportes filtrados por diversos criterios, reportes individuales
     * por ID, o reportes por rango de fechas para todos los usuarios.
     * 
     * @param costSort Criterio de ordenamiento por costo
     * @param dateFilter Filtro de fecha (hoy, semana, mes)
     * @param inspector Filtro por inspector
     * @param lote Filtro por lote
     * @param currency Moneda para mostrar valores monetarios
     * @param format Formato de exportación (pdf, excel, csv)
     * @param idUsuario ID de usuario para filtrar reportes
     * @param reporteId ID de un reporte específico para exportar solo ese
     * @param allUsers Indicador para incluir reportes de todos los usuarios
     * @param fechaInicio Fecha inicial para filtro por rango
     * @param fechaFin Fecha final para filtro por rango
     * @param response Objeto HttpServletResponse para escribir el documento exportado
     * @throws IOException Si ocurre un error durante la escritura del documento
     */
    @GetMapping("/exportar")
    public void exportarReportes(
            @RequestParam(required = false) String costSort,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String inspector,
            @RequestParam(required = false) String lote,
            @RequestParam(defaultValue = "USD") String currency,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long reporteId,
            @RequestParam(required = false) String allUsers, // New parameter to include all users
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            HttpServletResponse response) throws IOException {

        try {
            // Configurar respuesta
            String contentType = "application/pdf";
            String filename = reporteId != null ? 
                    "reporte_" + reporteId + ".pdf" : "reportes.pdf";

            if ("excel".equals(format)) {
                contentType = "application/vnd.ms-excel";
                filename = reporteId != null ? 
                        "reporte_" + reporteId + ".xlsx" : "reportes.xlsx";
            } else if ("csv".equals(format)) {
                contentType = "text/csv";
                filename = reporteId != null ? 
                        "reporte_" + reporteId + ".csv" : "reportes.csv";
            }

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

            // Check if we need to export a single report by ID
            if (reporteId != null) {
                reporteServicio.exportarReportePorId(reporteId, currency, response.getOutputStream());
                return;
            }
            
            // Check if we're exporting by date range for all users
            if (fechaInicio != null && fechaFin != null && "true".equals(allUsers)) {
                reporteServicio.exportarReportesPorFechas(fechaInicio, fechaFin, currency, response.getOutputStream());
                return;
            }

            // If no specific report ID or date range for all users, proceed with filtered export
            Map<String, String> params = new HashMap<>();
            params.put("costSort", costSort != null ? costSort : "");
            params.put("dateFilter", dateFilter != null ? dateFilter : "");
            params.put("inspector", inspector != null ? inspector : "");
            params.put("lote", lote != null ? lote : "");
            params.put("format", format);
            
            // For date range filtering with user ID
            if (fechaInicio != null && fechaFin != null) {
                params.put("fechaInicio", fechaInicio);
                params.put("fechaFin", fechaFin);
            }

            // Use default user ID if not provided
            if (idUsuario == null) {
                idUsuario = 1L; // Default user ID for testing
            }
            params.put("idUsuario", idUsuario.toString());

            // Generate the filtered report
            reporteServicio.exportarReportes(params, currency, response.getOutputStream());

        } catch (IOException e) {
            // Send error response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/html");
            response.getWriter().write("<html><body><h2>Error al generar el reporte</h2>" +
                                        "<p>" + e.getMessage() + "</p>" +
                                        "<p>Detalles: " + e.getClass().getName() + "</p>" +
                                        "</body></html>");
        }
    }

    /**
     * Endpoint para obtener todos los reportes con información resumida.
     * 
     * @return ResponseEntity con lista de DTOs de reportes
     */
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> getTodosReportes() {
        List<Reporte> reportes = reporteServicio.getTodosReportesConDetalles();

        List<ReporteDTO> dtos = reportes.stream().map(reporte -> {
            ReporteDTO dto = new ReporteDTO();
            dto.setIdReporte(reporte.getIdReporte());
            dto.setLoteId(reporte.getLoteId());
            dto.setInspector(reporte.getInspector());
            dto.setCostoTotal(reporte.getCostoTotal());
            dto.setFecha(reporte.getFecha());

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

    /**
     * Obtiene la lista de inspectores únicos para los filtros de UI.
     * 
     * @return ResponseEntity con la lista de nombres de inspectores
     */
    @GetMapping("/inspectores")
    public ResponseEntity<List<String>> getInspectores() {
        List<String> inspectores = reporteServicio.getInspectoresUnicos();
        return ResponseEntity.ok(inspectores);
    }
    
    /**
     * Obtiene la lista de lotes únicos para los filtros de UI.
     * 
     * @return ResponseEntity con la lista de IDs de lotes
     */
    @GetMapping("/lotes")
    public ResponseEntity<List<String>> getLotes() {
        List<String> lotes = reporteServicio.getLotesUnicos();
        return ResponseEntity.ok(lotes);
    }
}
