package com.example.servicio_conversion_moneda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 *
 * DTO para la respuesta de nuestro endpoint /rate
 *
 * @author PC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateResponse {

    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRate;
    private LocalDate date;
}
