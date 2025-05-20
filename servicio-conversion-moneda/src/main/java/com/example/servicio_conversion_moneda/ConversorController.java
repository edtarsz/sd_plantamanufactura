package com.example.servicio_conversion_moneda;

import com.example.servicio_conversion_moneda.dto.ConversionResponse;
import com.example.servicio_conversion_moneda.dto.RateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
/**
 * Controlador REST para operaciones de conversión de moneda.
 *
 * <p>Expone endpoints para realizar conversiones entre monedas y obtener tasas de cambio actuales
 * utilizando el servicio {@link ConversorService}.</p>
 *
 * <p>Base URL: <code>/api/v1/conversion</code></p>
 *
 * @author PC
 */
@RestController
@RequestMapping("/api/v1/conversion")
public class ConversorController {

    private final ConversorService conversorService;

    /**
     * Constructor que inyecta el servicio de conversión de moneda.
     *
     * @param conversorService el servicio que contiene la lógica de negocio
     */
    @Autowired
    public ConversorController(ConversorService conversorService) {
        this.conversorService = conversorService;
    }

    /**
     * Endpoint para convertir una cantidad entre dos monedas.
     * 
     * <p>Ejemplo: <code>GET /api/v1/conversion/convert?from=USD&to=MXN&amount=100</code></p>
     *
     * @param from moneda de origen (ej. "USD")
     * @param to moneda de destino (ej. "MXN")
     * @param amount cantidad a convertir
     * @return resultado de la conversión o mensaje de error
     */
    @GetMapping("/convert")
    public ResponseEntity<?> convertirMoneda(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("La cantidad debe ser un número positivo.");
        }
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Las monedas de origen y destino son requeridas.");
        }

        ConversionResponse resultado = conversorService.convertir(from, to, amount);

        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(503).body("No se pudo realizar la conversión en este momento. Verifique las monedas o intente más tarde.");
        }
    }

    /**
     * Endpoint para obtener la tasa de cambio entre dos monedas.
     * 
     * <p>Ejemplo: <code>GET /api/v1/conversion/rate?from=USD&to=MXN</code></p>
     *
     * @param from moneda de origen
     * @param to moneda de destino
     * @return objeto {@link RateResponse} o mensaje de error
     */
    @GetMapping("/rate")
    public ResponseEntity<?> obtenerTasa(
            @RequestParam String from,
            @RequestParam String to) {

        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Las monedas de origen y destino son requeridas.");
        }

        RateResponse tasa = conversorService.obtenerTasaDeCambio(from, to);

        if (tasa != null) {
            return ResponseEntity.ok(tasa);
        } else {
            return ResponseEntity.status(503).body("No se pudo obtener la tasa de cambio. Verifique las monedas o intente más tarde.");
        }
    }
}
