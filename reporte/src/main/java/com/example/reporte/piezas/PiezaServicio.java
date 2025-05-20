/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.piezas;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar la lógica de negocio relacionada con la entidad
 * Pieza. Provee métodos para obtener, guardar, actualizar y eliminar piezas
 * desde la base de datos.
 *
 * Este servicio actúa como intermediario entre el controlador y el repositorio.
 * Utiliza inyección de dependencias a través de constructor gracias a Lombok
 * (@AllArgsConstructor).
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class PiezaServicio {

    private final PiezaRepository piezaRepository;

    /**
     * Recupera todas las piezas registradas.
     *
     * @return lista de objetos Pieza
     */
    public List<Pieza> getPiezas() {
        return piezaRepository.findAll();
    }

    /**
     * Recupera una pieza específica por su ID.
     *
     * @param id identificador de la pieza
     * @return un Optional con la pieza encontrada o vacío si no existe
     */
    public Optional<Pieza> getPieza(Long id) {
        return piezaRepository.findById(id);
    }

    /**
     * Guarda una nueva pieza o actualiza una existente.
     *
     * @param pieza objeto Pieza a guardar o actualizar
     */
    public void saveOrUpdate(Pieza pieza) {
        piezaRepository.save(pieza);
    }

    /**
     * Elimina una pieza por su ID.
     *
     * @param id identificador de la pieza a eliminar
     */
    public void delete(Long id) {
        piezaRepository.deleteById(id);
    }
}
