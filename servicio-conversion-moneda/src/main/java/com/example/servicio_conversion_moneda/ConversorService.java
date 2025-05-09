/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.servicio_conversion_moneda;

import com.example.servicio_conversion_moneda.dto.FrankfurterResponse;
import com.example.servicio_conversion_moneda.dto.ConversionResponse;
import com.example.servicio_conversion_moneda.dto.RateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;


/**
 *
 * @author PC
 */
@Service
public class ConversorService {

    private final RestTemplate restTemplate;

    @Value("${external.api.exchangerate.base-url:https://api.frankfurter.app}")
    private String frankfurterApiBaseUrl;

    @Autowired
    public ConversorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtiene la tasa de cambio entre dos monedas desde la API de Frankfurter.
     *
     * @param monedaOrigen Código de la moneda de origen (ej. "USD")
     * @param monedaDestino Código de la moneda de destino (ej. "MXN")
     * @return RateResponse con la tasa de cambio o null si hay error.
     */
    public RateResponse obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) {
        String url = UriComponentsBuilder.fromHttpUrl(frankfurterApiBaseUrl)
                .path("/latest")
                .queryParam("from", monedaOrigen.toUpperCase())
                .queryParam("to", monedaDestino.toUpperCase())
                .toUriString();

        try {
            FrankfurterResponse response = restTemplate.getForObject(url, FrankfurterResponse.class);

            if (response != null && response.getRates() != null && response.getRates().containsKey(monedaDestino.toUpperCase())) {
                BigDecimal tasa = response.getRates().get(monedaDestino.toUpperCase());
                return new RateResponse(monedaOrigen.toUpperCase(), monedaDestino.toUpperCase(), tasa, response.getDate());
            } else {
                System.err.println("Respuesta inesperada de la API o tasa no encontrada para " + monedaDestino);
                return null;
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error al llamar a la API de Frankfurter (" + url + "): " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("Error inesperado al obtener tasa de cambio (" + url + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convierte una cantidad de una moneda a otra usando la tasa obtenida.
     *
     * @param monedaOrigen Código de la moneda de origen
     * @param monedaDestino Código de la moneda de destino
     * @param cantidad La cantidad a convertir
     * @return ConversionResponse con el resultado o null si hay error.
     */
    public ConversionResponse convertir(String monedaOrigen, String monedaDestino, BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("Cantidad inválida para convertir: " + cantidad);
            return null;
        }
        if (monedaOrigen.equalsIgnoreCase(monedaDestino)) {
            return new ConversionResponse(monedaOrigen.toUpperCase(), monedaDestino.toUpperCase(), cantidad, BigDecimal.ONE, cantidad, LocalDate.now());
        }

        RateResponse tasaInfo = obtenerTasaDeCambio(monedaOrigen, monedaDestino);

        if (tasaInfo != null && tasaInfo.getExchangeRate() != null) {
            BigDecimal tasa = tasaInfo.getExchangeRate();
            BigDecimal cantidadConvertida = cantidad.multiply(tasa).setScale(4, RoundingMode.HALF_UP);

            return new ConversionResponse(
                    monedaOrigen.toUpperCase(),
                    monedaDestino.toUpperCase(),
                    cantidad,
                    tasa,
                    cantidadConvertida,
                    tasaInfo.getDate()
            );
        } else {
            return null;
        }
    }
}

