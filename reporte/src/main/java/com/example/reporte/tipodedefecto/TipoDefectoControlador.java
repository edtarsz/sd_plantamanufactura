/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

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
@RequestMapping(path = "/api/v1/tipo-defectos")
public class TipoDefectoControlador {

    @Autowired
    private final TipoDefectoServicio tipoDefectoServicio;

    public TipoDefectoControlador(TipoDefectoServicio tipoDefectoServicio) {
        this.tipoDefectoServicio = tipoDefectoServicio;
    }

    @GetMapping
    public List<TipoDefecto> getAll() {
        return tipoDefectoServicio.getTipoDefectos();
    }

    @GetMapping("/{idTipoDefecto}")
    public Optional<TipoDefecto> getByID(@PathVariable("idTipoDefecto") Long idTipoDefecto) {
        return tipoDefectoServicio.getTipoDefecto(idTipoDefecto);
    }

    @PostMapping
    public void saveOrUpdate(@RequestBody TipoDefecto tipoDefecto) {
        tipoDefectoServicio.saveOrUpdate(tipoDefecto);
    }

    @DeleteMapping("/{idTipoDefecto}")
    public void delete(@PathVariable("idTipoDefecto") Long idTipoDefecto) {
        tipoDefectoServicio.delete(idTipoDefecto);
    }
}
