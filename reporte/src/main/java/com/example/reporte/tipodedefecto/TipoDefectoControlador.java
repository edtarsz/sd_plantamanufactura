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
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la entidad TipoDefecto. Expone endpoints HTTP para
 * realizar operaciones CRUD sobre tipos de defecto.
 *
 * @author Ramos
 */
@RestController
@RequestMapping(path = "/api/v1/tipo-defectos")
public class TipoDefectoControlador {

    private final TipoDefectoServicio tipoDefectoServicio;

    /**
     * Constructor que inyecta el servicio de tipo de defecto.
     *
     * @param tipoDefectoServicio servicio que contiene la lógica de negocio
     */
    @Autowired
    public TipoDefectoControlador(TipoDefectoServicio tipoDefectoServicio) {
        this.tipoDefectoServicio = tipoDefectoServicio;
    }

    /**
     * Obtiene todos los tipos de defecto.
     *
     * @return lista de objetos TipoDefecto
     */
    @GetMapping
    public List<TipoDefecto> getAll() {
        return tipoDefectoServicio.getTipoDefectos();
    }

    /**
     * Obtiene un tipo de defecto por su ID.
     *
     * @param idTipoDefecto identificador del tipo de defecto
     * @return ResponseEntity con el objeto encontrado o 404 si no existe
     */
    @GetMapping("/{idTipoDefecto}")
    public ResponseEntity<TipoDefecto> getByID(@PathVariable("idTipoDefecto") Long idTipoDefecto) {
        return tipoDefectoServicio.getTipoDefecto(idTipoDefecto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo tipo de defecto.
     *
     * @param tipoDefecto objeto a crear (nombre obligatorio)
     * @return ResponseEntity con el objeto creado o error 400 si el nombre es
     * inválido
     */
    @PostMapping
    public ResponseEntity<TipoDefecto> create(@RequestBody TipoDefecto tipoDefecto) {
        if (tipoDefecto.getNombre() == null || tipoDefecto.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();  // Nombre es requerido
        }
        TipoDefecto nuevoDefecto = tipoDefectoServicio.saveOrUpdate(tipoDefecto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDefecto);
    }

    /**
     * Actualiza un tipo de defecto existente.
     *
     * @param id ID del tipo de defecto a actualizar
     * @param tipoDefecto datos nuevos
     * @return ResponseEntity con el objeto actualizado o 404 si no se encuentra
     */
    @PutMapping("/{idTipoDefecto}")
    public ResponseEntity<TipoDefecto> update(
            @PathVariable("idTipoDefecto") Long id,
            @RequestBody TipoDefecto tipoDefecto) {

        // Verificar existencia antes de actualizar
        if (!tipoDefectoServicio.getTipoDefecto(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        tipoDefecto.setIdTipoDefecto(id); // Asegurar que el ID se actualice correctamente
        TipoDefecto defectoActualizado = tipoDefectoServicio.saveOrUpdate(tipoDefecto);
        return ResponseEntity.ok(defectoActualizado);
    }

    /**
     * Elimina un tipo de defecto por su ID, si no tiene defectos asociados.
     *
     * @param idTipoDefecto ID del tipo a eliminar
     * @return 204 si se elimina con éxito, 409 si hay relaciones que lo impiden
     */
    @DeleteMapping("/{idTipoDefecto}")
    public ResponseEntity<?> delete(@PathVariable("idTipoDefecto") Long idTipoDefecto) {
        try {
            tipoDefectoServicio.delete(idTipoDefecto);
            return ResponseEntity.noContent().build();  // 204 No Content

        } catch (DataIntegrityViolationException e) {
            // 409 Conflict si hay dependencias que impiden eliminar
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
