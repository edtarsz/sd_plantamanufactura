/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.defectos;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class DefectoServicio {

    @Autowired
    DefectoRepository defectoRepository;

    public List<Defecto> getDefectos() {
        return defectoRepository.findAll();
    }

    public Optional<Defecto> getDefecto(Long id) {
        return defectoRepository.findById(id);
    }

    public void saveOrUpdate(Defecto defecto) {
        defectoRepository.save(defecto);
    }

    public void delete(Long id) {
        defectoRepository.deleteById(id);
    }
}
