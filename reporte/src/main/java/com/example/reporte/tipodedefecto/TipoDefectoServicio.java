/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.tipodedefecto;

import com.example.reporte.defectos.DefectoRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class TipoDefectoServicio {

    @Autowired
    private TipoDefectoRepository tipoDefectoRepositorio;

    @Autowired
    private DefectoRepository defectoRepositorio;

    @Autowired
    TipoDefectoRepository tipoDefectoRepository;

    public List<TipoDefecto> getTipoDefectos() {
        return tipoDefectoRepository.findAll();
    }

    public Optional<TipoDefecto> getTipoDefecto(Long id) {
        return tipoDefectoRepository.findById(id);
    }

    public TipoDefecto saveOrUpdate(TipoDefecto tipoDefecto) {
        // Validar nombre único al editar
        if (tipoDefecto.getIdTipoDefecto() != null) {
            Optional<TipoDefecto> existente = tipoDefectoRepositorio.findByNombreAndIdTipoDefectoNot(
                    tipoDefecto.getNombre(),
                    tipoDefecto.getIdTipoDefecto()
            );

            if (existente.isPresent()) {
                throw new DataIntegrityViolationException("Ya existe un tipo de defecto con ese nombre");
            }
        }
        return tipoDefectoRepositorio.save(tipoDefecto);
    }

    public void delete(Long idTipoDefecto) {
        if (defectoRepositorio.existsByTipoDefectoIdTipoDefecto(idTipoDefecto)) {
            throw new DataIntegrityViolationException("No se puede eliminar porque existen defectos asociados");
        }
        tipoDefectoRepositorio.deleteById(idTipoDefecto);
    }
}
