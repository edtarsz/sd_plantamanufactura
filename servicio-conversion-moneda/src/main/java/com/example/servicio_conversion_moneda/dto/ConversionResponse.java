package com.example.servicio_conversion_moneda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la respuesta de nuestro endpoint /convert
 * 
 * @author PC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResponse {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal originalAmount;
    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;
    private LocalDate date;
}
