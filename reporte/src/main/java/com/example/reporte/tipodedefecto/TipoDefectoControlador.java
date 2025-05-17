/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ramos
 *
 */
@RestController
@RequestMapping(path = "/api/v1/tipo-defectos")
public class TipoDefectoControlador {

    private final TipoDefectoServicio tipoDefectoServicio;

    @Autowired
    public TipoDefectoControlador(TipoDefectoServicio tipoDefectoServicio) {
        this.tipoDefectoServicio = tipoDefectoServicio;
    }

    @GetMapping
    public List<TipoDefecto> getAll() {
        return tipoDefectoServicio.getTipoDefectos();
    }

    @GetMapping("/{idTipoDefecto}")
    public ResponseEntity<TipoDefecto> getByID(@PathVariable("idTipoDefecto") Long idTipoDefecto) {
        return tipoDefectoServicio.getTipoDefecto(idTipoDefecto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoDefecto> create(@RequestBody TipoDefecto tipoDefecto) {
        if (tipoDefecto.getNombre() == null || tipoDefecto.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        TipoDefecto nuevoDefecto = tipoDefectoServicio.saveOrUpdate(tipoDefecto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDefecto);
    }

    @PutMapping("/{idTipoDefecto}")
    public ResponseEntity<TipoDefecto> update(
            @PathVariable("idTipoDefecto") Long id,
            @RequestBody TipoDefecto tipoDefecto) {

        if (!tipoDefectoServicio.getTipoDefecto(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        tipoDefecto.setIdTipoDefecto(id);
        TipoDefecto defectoActualizado = tipoDefectoServicio.saveOrUpdate(tipoDefecto);
        return ResponseEntity.ok(defectoActualizado);
    }

    @DeleteMapping("/{idTipoDefecto}")
    public ResponseEntity<?> delete(@PathVariable("idTipoDefecto") Long idTipoDefecto) {
        try {
            tipoDefectoServicio.delete(idTipoDefecto);
            return ResponseEntity.noContent().build();

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
