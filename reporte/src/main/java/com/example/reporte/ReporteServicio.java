/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import com.example.reporte.piezas.Pieza;
import com.example.reporte.piezas.PiezaRepository;
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
public class ReporteServicio {

    @Autowired
    ReporteRepository reporteRepository;

    @Autowired
    PiezaRepository piezaRepository;

    public List<Reporte> getReportes() {
        return reporteRepository.findAll();
    }

    public Optional<Reporte> getReporte(Long id) {
        return reporteRepository.findById(id);
    }

    public Reporte saveOrUpdate(Reporte reporte) {
        reporte.getDefectos().forEach(defecto -> {
            Pieza pieza = piezaRepository.findById(defecto.getPieza().getIdPieza())
                    .orElseThrow(() -> new RuntimeException("Pieza no encontrada"));
            defecto.setPieza(pieza);
        });

        return reporteRepository.save(reporte);
    }

    public void delete(Long id) {
        reporteRepository.deleteById(id);
    }

    public List<Reporte> findAllReportesByUsuario(Long idUsuario) {
        return reporteRepository.findAllReportesByIdUsuario(idUsuario);
    }
}
