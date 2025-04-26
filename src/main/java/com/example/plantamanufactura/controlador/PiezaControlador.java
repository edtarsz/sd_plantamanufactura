/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.controlador;

import com.example.plantamanufactura.entidades.Pieza;
import com.example.plantamanufactura.entidades.Reporte;
import com.example.plantamanufactura.servicio.PiezaServicio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ramos
 */
@RestController
@RequestMapping(path = "api/v1/piezas")
public class PiezaControlador {

    @Autowired
    private final PiezaServicio piezaServicio;

    public PiezaControlador(PiezaServicio piezaServicio) {
        this.piezaServicio = piezaServicio;
    }

    @GetMapping
    public List<Pieza> getAll() {
        return piezaServicio.getPiezas();
    }

    @GetMapping("/{idPieza}")
    public Optional<Pieza> getByID(@PathVariable("idPieza") Long idPieza) {
        return piezaServicio.getPieza(idPieza);
    }

    @PostMapping
    public void saveOrUpdate(@RequestBody Pieza pieza) {
        piezaServicio.saveOrUpdate(pieza);
    }

    @DeleteMapping("/{idPieza}")
    public void delete(@PathVariable("idPieza") Long idPieza) {
        piezaServicio.delete(idPieza);
    }
}
