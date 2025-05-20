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
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
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
        // Crear una especificación inicial sin filtro de usuario para depurar
        Specification<Reporte> spec = Specification.where(null);

        // Agregar filtro de usuario si se proporciona un ID válido
        if (idUsuario != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("idUsuario"), idUsuario));
        }

        if (filtros.containsKey("dateFilter")) {
            spec = spec.and(createDateSpecification(filtros.get("dateFilter")));
        }

        if (filtros.containsKey("inspector") && !filtros.get("inspector").isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("inspector"), filtros.get("inspector")));
        }

        if (filtros.containsKey("lote") && !filtros.get("lote").isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("loteId"), filtros.get("lote")));
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
        // Validar parámetro idUsuario primero
        String idUsuarioStr = params.get("idUsuario");
        if (idUsuarioStr == null || idUsuarioStr.isEmpty()) {
            throw new IllegalArgumentException("El parámetro idUsuario es requerido");
        }

        Long idUsuario;
        try {
            idUsuario = Long.valueOf(idUsuarioStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato inválido para idUsuario");
        }

        List<Reporte> reportes = filtrarReportes(params, idUsuario);

        // Si no hay reportes, asegurar que se muestre el mensaje en el PDF
        switch (params.get("format")) {
            case "pdf" ->
                generatePdfReport(reportes, currency, outputStream);
            default ->
                throw new IllegalArgumentException("Formato no soportado");
        }
    }

    public void generatePdfReport(List<Reporte> reportes, String currency, OutputStream outputStream) {
        Document document = new Document(PageSize.A4.rotate()); // Formato horizontal
        try {
            // Configurar metadatos
            document.addTitle("Reporte de defectos");
            document.addCreator("Sistema de Gestión de Calidad");

            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Agregar información de encabezado
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(44, 67, 86));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(100, 100, 100));

            PdfPCell titleCell = new PdfPCell(new Phrase("Reporte de Defectos", titleFont));
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setBorder(PdfPCell.NO_BORDER);
            titleCell.setPaddingBottom(10);

            PdfPCell dateCell = new PdfPCell(new Phrase("Fecha de generación: "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), dateFont));
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setBorder(PdfPCell.NO_BORDER);
            dateCell.setPaddingBottom(20);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.addCell(titleCell);
            headerTable.addCell(dateCell);
            document.add(headerTable);

            // Si no hay reportes, mostrar mensaje
            if (reportes.isEmpty()) {
                Font messageFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.RED);
                document.add(new Phrase("No se encontraron reportes que coincidan con los criterios de búsqueda.",
                        messageFont));
                return;
            }

            // Formatters para valores numéricos y fechas
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
                    currency.equals("MXN") ? new Locale("es", "MX") : Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            // Si hay un solo reporte, mostrar una vista detallada
            if (reportes.size() == 1) {
                generateDetailedSingleReport(reportes.get(0), document, currency, currencyFormat, dateFormat,
                        subtitleFont, sectionFont, headerFont, bodyFont, labelFont);
            } else {
                // Si hay múltiples reportes, mostrar una vista resumida de todos
                generateSummaryReport(reportes, document, currency, currencyFormat, dateFormat,
                        subtitleFont, headerFont, bodyFont);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Genera un reporte detallado para un solo reporte
     */
    private void generateDetailedSingleReport(Reporte reporte, Document document, String currency,
            NumberFormat currencyFormat, SimpleDateFormat dateFormat, Font subtitleFont, Font sectionFont,
            Font headerFont, Font bodyFont, Font labelFont) throws DocumentException {
        // Agregar información general del reporte
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);

        // Título del reporte individual
        PdfPCell reportTitleCell = new PdfPCell(new Phrase("Detalle del Reporte #" + reporte.getIdReporte(), subtitleFont));
        reportTitleCell.setColspan(2);
        reportTitleCell.setBorder(PdfPCell.NO_BORDER);
        reportTitleCell.setPaddingBottom(10);
        infoTable.addCell(reportTitleCell);

        // Columna izquierda: Información general
        PdfPTable leftInfoTable = new PdfPTable(2);
        leftInfoTable.setWidthPercentage(100);

        addInfoRow(leftInfoTable, "ID de Reporte:", reporte.getIdReporte().toString(), labelFont, bodyFont);
        addInfoRow(leftInfoTable, "Lote:", reporte.getLoteId(), labelFont, bodyFont);
        addInfoRow(leftInfoTable, "Inspector:", reporte.getInspector(), labelFont, bodyFont);
        addInfoRow(leftInfoTable, "Fecha:", dateFormat.format(reporte.getFecha()), labelFont, bodyFont);

        // Columna derecha: Costos y totales
        PdfPTable rightInfoTable = new PdfPTable(2);
        rightInfoTable.setWidthPercentage(100);

        double costoConvertido = convertCurrency(reporte.getCostoTotal().doubleValue(), currency);
        int totalPiezas = reporte.getDefectos().stream()
                .mapToInt(d -> d.getCantidad_piezas())
                .sum();

        addInfoRow(rightInfoTable, "Total Defectos:", String.valueOf(reporte.getDefectos().size()), labelFont, bodyFont);
        addInfoRow(rightInfoTable, "Total Piezas Defectuosas:", String.valueOf(totalPiezas), labelFont, bodyFont);
        addInfoRow(rightInfoTable, "Costo Total (" + currency + "):", currencyFormat.format(costoConvertido), labelFont, bodyFont);
        addInfoRow(rightInfoTable, "ID de Usuario:", reporte.getIdUsuario().toString(), labelFont, bodyFont);

        // Agregar las dos columnas a la tabla principal
        PdfPCell leftCell = new PdfPCell(leftInfoTable);
        leftCell.setBorder(PdfPCell.NO_BORDER);
        infoTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell(rightInfoTable);
        rightCell.setBorder(PdfPCell.NO_BORDER);
        infoTable.addCell(rightCell);

        document.add(infoTable);

        // Sección de defectos
        Paragraph defectTitle = new Paragraph("Listado de Defectos", sectionFont);
        defectTitle.setSpacingBefore(10);
        defectTitle.setSpacingAfter(10);
        document.add(defectTitle);

        // Tabla de defectos
        if (!reporte.getDefectos().isEmpty()) {
            PdfPTable defectsTable = new PdfPTable(new float[]{1f, 3f, 2f, 2f, 2f, 3f});
            defectsTable.setWidthPercentage(100);

            // Encabezados de la tabla de defectos
            String[] defectHeaders = {"#", "Tipo de Defecto", "Pieza", "Cantidad", "Costo (" + currency + ")", "Detalles"};
            for (String header : defectHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(44, 67, 86));
                cell.setBorderWidth(1f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5f);
                defectsTable.addCell(cell);
            }

            // Contenido de la tabla de defectos
            int index = 1;
            for (Defecto defecto : reporte.getDefectos()) {
                // Número
                defectsTable.addCell(createCell(String.valueOf(index++), bodyFont));

                // Tipo de defecto
                defectsTable.addCell(createCell(defecto.getTipoDefecto().getNombre(), bodyFont));

                // Pieza
                defectsTable.addCell(createCell(defecto.getPieza().getNombre(), bodyFont));

                // Cantidad
                defectsTable.addCell(createCell(String.valueOf(defecto.getCantidad_piezas()), bodyFont));

                // Costo
                double defectoCostoConvertido = convertCurrency(defecto.getCosto().doubleValue(), currency);
                defectsTable.addCell(createCell(currencyFormat.format(defectoCostoConvertido), bodyFont));

                // Detalles
                PdfPCell detailsCell = new PdfPCell(new Phrase(defecto.getDetalles(), bodyFont));
                detailsCell.setPadding(5f);
                detailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                defectsTable.addCell(detailsCell);
            }

            document.add(defectsTable);
        } else {
            document.add(new Paragraph("No hay defectos registrados en este reporte.", bodyFont));
        }
    }

    /**
     * Genera un reporte resumido para múltiples reportes
     */
    private void generateSummaryReport(List<Reporte> reportes, Document document, String currency,
            NumberFormat currencyFormat, SimpleDateFormat dateFormat,
            Font subtitleFont, Font headerFont, Font bodyFont) throws DocumentException {

        // Título de la sección
        Paragraph summaryTitle = new Paragraph("Resumen de Reportes", subtitleFont);
        summaryTitle.setSpacingBefore(10);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);

        // Tabla principal de reportes
        PdfPTable table = new PdfPTable(6); // Añadimos una columna más para el total de defectos
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 2f, 2f, 1.5f, 2f});

        // Encabezados de tabla
        String[] headers = {"ID", "Lote", "Inspector", "Fecha", "Total Defectos", "Costo (" + currency + ")"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(44, 67, 86)); // Color #2c4356 del tema
            cell.setBorderWidth(1f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        // Contenido
        for (Reporte reporte : reportes) {
            table.addCell(createCell(reporte.getIdReporte().toString(), bodyFont));

            table.addCell(createCell(reporte.getLoteId(), bodyFont));
            table.addCell(createCell(reporte.getInspector(), bodyFont));
            table.addCell(createCell(dateFormat.format(reporte.getFecha()), bodyFont));

            int totalDefectos = reporte.getDefectos().size();
            table.addCell(createCell(String.valueOf(totalDefectos), bodyFont));

            double costoConvertido = convertCurrency(reporte.getCostoTotal().doubleValue(), currency);
            table.addCell(createCell(currencyFormat.format(costoConvertido), bodyFont));
        }

        document.add(table);

        // Agregar resumen final
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(40);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setSpacingBefore(20);

        // Total de reportes
        PdfPCell labelCell = new PdfPCell(new Phrase("Total de reportes:", bodyFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        summaryTable.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(reportes.size()), bodyFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(valueCell);

        // Total de defectos
        int totalDefectos = reportes.stream()
                .mapToInt(r -> r.getDefectos().size())
                .sum();

        labelCell = new PdfPCell(new Phrase("Total de defectos:", bodyFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        summaryTable.addCell(labelCell);

        valueCell = new PdfPCell(new Phrase(String.valueOf(totalDefectos), bodyFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(valueCell);

        // Costo total
        double costoTotal = reportes.stream()
                .mapToDouble(r -> convertCurrency(r.getCostoTotal().doubleValue(), currency))
                .sum();

        labelCell = new PdfPCell(new Phrase("Costo total:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK)));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        summaryTable.addCell(labelCell);

        valueCell = new PdfPCell(new Phrase(currencyFormat.format(costoTotal),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK)));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(valueCell);

        document.add(summaryTable);
    }

    /**
     * Añade una fila de información con etiqueta y valor a una tabla
     */
    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPadding(5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPadding(5f);
        table.addCell(valueCell);
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private double convertCurrency(Double amount, String targetCurrency) {
        if (amount == null) {
            return 0.0;
        }

        // Si la moneda objetivo es USD, no es necesario convertir
        if ("USD".equalsIgnoreCase(targetCurrency)) {
            return amount;
        }

        try {
            // Hacer la solicitud al servicio de conversión
            String url = "http://servicio-conversion-moneda/api/v1/conversion/rate?from=USD&to=" + targetCurrency;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() != null && response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();

                // Verificar si existe la clave exchangeRate
                if (responseBody.containsKey("exchangeRate")) {
                    double exchangeRate = ((Number) responseBody.get("exchangeRate")).doubleValue();
                    return amount * exchangeRate;
                } else {
                    return useFallbackRate(amount, targetCurrency);
                }
            } else {
                return useFallbackRate(amount, targetCurrency);
            }
        } catch (RestClientException e) {
            // Usar fallback y continuar sin romper la generación del PDF
            return useFallbackRate(amount, targetCurrency);
        }
    }

    private double useFallbackRate(Double amount, String targetCurrency) {
        // Valores por defecto para diferentes monedas
        double rate = switch (targetCurrency.toUpperCase()) {
            case "MXN" ->
                17.05; // Actualizado (aprox.)
            case "EUR" ->
                0.92;  // Actualizado (aprox.)
            case "GBP" ->
                0.78;  // Libra esterlina (aprox.)
            case "JPY" ->
                149.0; // Yen japonés (aprox.)
            case "CAD" ->
                1.35;  // Dólar canadiense (aprox.)
            default ->
                1.0;      // Si no sabemos la moneda, no convertimos
        };

        return amount * rate;
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
                    idUsuario);

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

    /**
     * Exporta un único reporte por su ID
     */
    public void exportarReportePorId(Long reporteId, String currency, OutputStream outputStream) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(reporteId);

        if (reporteOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el reporte con ID: " + reporteId);
        }

        Reporte reporte = reporteOpt.get();
        List<Reporte> reportes = List.of(reporte);

        // Usar el mismo método de generación de PDF
        generatePdfReport(reportes, currency, outputStream);
    }
}
