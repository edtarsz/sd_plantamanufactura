/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import com.example.exception.ServicioNoDisponibleException;
import com.example.exception.UsuarioNoEncontradoException;
import com.example.reporte.defectos.Defecto;
import com.example.reporte.piezas.Pieza;
import com.example.reporte.piezas.PiezaRepository;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class ReporteServicio {

    @Autowired
    ReporteRepository reporteRepository;

    @Autowired
    PiezaRepository piezaRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Reporte> getReportes() {
        return reporteRepository.findAll();
    }

    public Optional<Reporte> getReporte(Long id) {
        return reporteRepository.findById(id);
    }

    public List<Reporte> findAllByUsuarioId(Long idUsuario) {
        return reporteRepository.findAllByUsuarioId(idUsuario);
    }

    public Reporte saveOrUpdate(Reporte reporte) {
        double costoTotal = 0;

        if (reporte.getIdReporte() == null) {
            reporte.setFecha(new Date());
        }

        for (Defecto defecto : reporte.getDefectos()) {
            // Obtener pieza actual de la base de datos
            Pieza pieza = piezaRepository.findById(defecto.getPieza().getIdPieza())
                    .orElseThrow(() -> new RuntimeException("Pieza no encontrada"));

            // Recalcular costo usando el precio actual de la pieza
            double costoDefecto = pieza.getCosto() * defecto.getCantidad_piezas();
            defecto.setCosto(costoDefecto);
            costoTotal += costoDefecto;
        }

        reporte.setCostoTotal(costoTotal);
        return reporteRepository.save(reporte);
    }

    public List<Reporte> getTodosReportesConDetalles() {
        return reporteRepository.findAllWithDefectos();
    }

    public void delete(Long id) {
        reporteRepository.deleteById(id);
    }

    public List<Reporte> filtrarReportes(Map<String, String> filtros, Long idUsuario) {
        Specification<Reporte> spec = Specification.where((root, query, cb)
                -> cb.equal(root.get("idUsuario"), idUsuario));

        if (filtros.containsKey("dateFilter")) {
            spec = spec.and(createDateSpecification(filtros.get("dateFilter")));
        }

        if (filtros.containsKey("inspector") && !filtros.get("inspector").isEmpty()) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("inspector"), filtros.get("inspector")));
        }

        if (filtros.containsKey("lote") && !filtros.get("lote").isEmpty()) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("loteId"), filtros.get("lote")));
        }

        Sort sort = getSortDirection(filtros.getOrDefault("costSort", ""));

        return reporteRepository.findAll(spec, sort);
    }

    private Specification<Reporte> createDateSpecification(String dateFilter) {
        return (root, query, cb) -> {
            Date startDate = getStartDate(dateFilter);
            return cb.greaterThanOrEqualTo(root.get("fecha"), startDate);
        };
    }

    private Date getStartDate(String filter) {
        LocalDate now = LocalDate.now();
        return switch (filter) {
            case "hoy" ->
                Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
            case "semana" ->
                Date.from(now.minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            case "mes" ->
                Date.from(now.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            default ->
                Date.from(now.minusYears(10).atStartOfDay(ZoneId.systemDefault()).toInstant()); // Todos los registros
        };
    }

    private Sort getSortDirection(String costSort) {
        return switch (costSort) {
            case "loteA" ->
                Sort.by(Sort.Direction.ASC, "costoTotal");
            case "loteB" ->
                Sort.by(Sort.Direction.DESC, "costoTotal");
            default ->
                Sort.unsorted();
        };
    }

    public void exportarReportes(Map<String, String> params, String currency, OutputStream outputStream) {
        List<Reporte> reportes = filtrarReportes(params, Long.parseLong(params.get("idUsuario")));

        switch (params.get("format")) {
            case "pdf" ->
                generatePdfReport(reportes, currency, outputStream);
            default ->
                throw new IllegalArgumentException("Formato no soportado");
        }
    }

    public void generatePdfReport(List<Reporte> reportes, String currency, OutputStream outputStream) {
        com.lowagie.text.Document document = new com.lowagie.text.Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Verificar si hay reportes
            if (reportes.isEmpty()) {
                document.add(new Paragraph("No hay reportes para mostrar"));
                return;
            }

            // Obtener información del usuario
            UsuarioDTO usuario = obtenerUsuarioDesdeMicroservicio(reportes.get(0).getIdUsuario());

            // Crear tabla
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 3f, 3f, 3f});

            // Encabezados
            addTableHeader(table, currency);

            // Contenido
            double total = 0;
            for (Reporte reporte : reportes) {
                addReportRow(table, reporte, currency);
                total += convertCurrency(reporte.getCostoTotal(), currency);
            }

            // Total
            addTotalRow(table, total, currency);

            document.add(table);

        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String currency) {
        String[] headers = {"ID", "Lote", "Inspector", "Fecha", "Costo (" + currency + ")"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(new Color(240, 240, 240)); // Gris claro
            cell.setBorderWidth(1f);
            table.addCell(cell);
        }
    }

    private void addReportRow(PdfPTable table, Reporte reporte, String currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance(
                currency.equals("MXN") ? new Locale("es", "MX") : Locale.US
        );

        table.addCell(createCell(reporte.getIdReporte().toString()));
        table.addCell(createCell(reporte.getLoteId()));
        table.addCell(createCell(reporte.getInspector()));
        table.addCell(createCell(new SimpleDateFormat("dd/MM/yyyy").format(reporte.getFecha())));
        table.addCell(createCell(format.format(convertCurrency(reporte.getCostoTotal(), currency))));
    }

    private void addTotalRow(PdfPTable table, double total, String currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance(
                currency.equals("MXN") ? new Locale("es", "MX") : Locale.US
        );

        PdfPCell cell = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setColspan(4);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(0);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(format.format(total), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private PdfPCell createCell(String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setPadding(5f);
        return cell;
    }

    private double convertCurrency(Double amount, String targetCurrency) {
        if ("USD".equalsIgnoreCase(targetCurrency)) {
            return amount;
        }

        try {
            ResponseEntity<Double> response = restTemplate.getForEntity(
                    "http://SERVICIO-CONVERSION-MONEDA/api/v1/conversion/rate?from=USD&to=MXN",
                    Double.class
            );

            return amount * response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error en conversión: " + e.getMessage());
        }
    }

    public List<Reporte> getReportesConDetalles(Long usuarioId) {
        return reporteRepository.findByUsuarioIdWithDefectos(usuarioId);
    }

    public UsuarioDTO obtenerUsuarioDesdeMicroservicio(Long idUsuario) {
        String url = "http://usuarios/api/v1/usuarios/{id}";

        try {
            ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    UsuarioDTO.class,
                    idUsuario
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new UsuarioNoEncontradoException(idUsuario);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new UsuarioNoEncontradoException(idUsuario);
        } catch (RestClientException ex) {
            throw new ServicioNoDisponibleException("Servicio de Usuarios");
        }
    }
}
