package com.example.servicio_conversion_moneda;

import com.example.servicio_conversion_moneda.dto.ConversionResponse;
import com.example.servicio_conversion_moneda.dto.RateResponse;
import com.example.servicio_conversion_moneda.ConversorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/**
 *
 * @author PC
 */
@RestController
@RequestMapping("/api/v1/conversion")
public class ConversorController {

    private final ConversorService conversorService;

    @Autowired
    public ConversorController(ConversorService conversorService) {
        this.conversorService = conversorService;
    }

    //GET /api/v1/conversion/convert?from=USD&to=MXN&amount=100
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

    //GET /api/v1/conversion/rate?from=USD&to=MXN
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
