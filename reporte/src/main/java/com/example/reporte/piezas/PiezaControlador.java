/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.piezas;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la entidad Pieza. Proporciona endpoints HTTP para
 * realizar operaciones CRUD sobre piezas.
 *
 * Este controlador maneja solicitudes relacionadas con la entidad Pieza,
 * incluyendo la obtención, creación/actualización y eliminación de registros.
 *
 *
 * @author Ramos
 */
@RestController
@RequestMapping(path = "api/v1/piezas")
public class PiezaControlador {

    @Autowired
    private final PiezaServicio piezaServicio;

    /**
     * Constructor que inyecta el servicio de pieza.
     *
     * @param piezaServicio Servicio que contiene la lógica de negocio
     * relacionada con piezas.
     */
    public PiezaControlador(PiezaServicio piezaServicio) {
        this.piezaServicio = piezaServicio;
    }

    /**
     * Obtiene todas las piezas disponibles.
     *
     * @return lista de objetos Pieza.
     */
    @GetMapping
    public List<Pieza> getAll() {
        return piezaServicio.getPiezas();
    }

    /**
     * Obtiene una pieza por su identificador.
     *
     * @param idPieza ID de la pieza a buscar.
     * @return objeto Pieza si se encuentra, o un Optional vacío.
     */
    @GetMapping("/{idPieza}")
    public Optional<Pieza> getByID(@PathVariable("idPieza") Long idPieza) {
        return piezaServicio.getPieza(idPieza);
    }

    /**
     * Guarda una nueva pieza o actualiza una existente.
     *
     * @param pieza Objeto Pieza recibido desde el cuerpo de la solicitud.
     */
    @PostMapping
    public void saveOrUpdate(@RequestBody Pieza pieza) {
        piezaServicio.saveOrUpdate(pieza);
    }

    /**
     * Elimina una pieza por su ID.
     *
     * @param idPieza ID de la pieza a eliminar.
     */
    @DeleteMapping("/{idPieza}")
    public void delete(@PathVariable("idPieza") Long idPieza) {
        piezaServicio.delete(idPieza);
    }
}
