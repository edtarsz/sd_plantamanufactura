/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import com.example.reporte.defectos.Defecto;
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
public class TipoDefectoServicio {

    @Autowired
    TipoDefectoRepository tipoDefectoRepository;

    public List<TipoDefecto> getTipoDefectos() {
        return tipoDefectoRepository.findAll();
    }

    public Optional<TipoDefecto> getTipoDefecto(Long id) {
        return tipoDefectoRepository.findById(id);
    }

    public void saveOrUpdate(TipoDefecto tipoDefecto) {
        tipoDefectoRepository.save(tipoDefecto);
    }

    public void delete(Long id) {
        tipoDefectoRepository.deleteById(id);
    }
}
