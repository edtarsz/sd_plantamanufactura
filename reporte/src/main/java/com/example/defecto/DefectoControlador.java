/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.defecto;

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
@RequestMapping(path = "/api/v1/defectos")
public class DefectoControlador {

    @Autowired
    private final DefectoServicio defectoServicio;

    public DefectoControlador(DefectoServicio defectoServicio) {
        this.defectoServicio = defectoServicio;
    }

    @GetMapping
    public List<Defecto> getAll() {
        return defectoServicio.getDefectos();
    }

    @GetMapping("/{idDefecto}")
    public Optional<Defecto> getByID(@PathVariable("idDefecto") Long idDefecto) {
        return defectoServicio.getDefecto(idDefecto);
    }

    @PostMapping
    public void saveOrUpdate(@RequestBody Defecto defecto) {
        defectoServicio.saveOrUpdate(defecto);
    }

    @DeleteMapping("/{idDefecto}")
    public void delete(@PathVariable("idDefecto") Long idDefecto) {
        defectoServicio.delete(idDefecto);
    }
}
