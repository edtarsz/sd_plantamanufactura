/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte.defectos;

import com.example.reporte.piezas.Pieza;
import com.example.reporte.tipodedefecto.TipoDefecto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "defecto")
public class Defecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDefecto")
    private Long idDefecto;

    @Column(name = "detalles", nullable = false)
    private String detalles;

    @Column(name = "costo", nullable = false)
    private Float costo;

    @Column(name = "cantidad_piezas", nullable = false)
    private int cantidad_piezas;

    @ManyToOne
    @JoinColumn(name = "idTipoDefecto")
    private TipoDefecto tipoDefecto;

    @ManyToOne
    @JoinColumn(name = "idPieza")
    private Pieza pieza;
}
