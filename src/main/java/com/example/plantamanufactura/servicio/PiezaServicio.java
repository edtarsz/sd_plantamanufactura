/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.servicio;

import com.example.plantamanufactura.entidades.Pieza;
import com.example.plantamanufactura.repositorio.PiezaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
public class PiezaServicio {

    @Autowired
    PiezaRepository piezaRepository;

    public List<Pieza> getPiezas() {
        return piezaRepository.findAll();
    }

    public Optional<Pieza> getPieza(Long id) {
        return piezaRepository.findById(id);
    }

    public void saveOrUpdate(Pieza pieza) {
        piezaRepository.save(pieza);
    }

    public void delete(Long id) {
        piezaRepository.deleteById(id);
    }
}
