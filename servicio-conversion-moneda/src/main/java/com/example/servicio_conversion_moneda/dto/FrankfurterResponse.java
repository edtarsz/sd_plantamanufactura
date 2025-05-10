package com.example.servicio_conversion_moneda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para mapear la respuesta completa de la API de Frankfurter.
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrankfurterResponse {

    private BigDecimal amount;
    private String base;
    private LocalDate date;
    private Map<String, BigDecimal> rates;

}
