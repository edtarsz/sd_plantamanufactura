/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.defectos;

import com.example.tipodedefecto.TipoDeDefecto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "costo", nullable = false)
    private Float costo;

    @Column(name = "detalles", nullable = false)
    private String detalles;

    @Column(name = "cantidad_piezas", nullable = false)
    private int cantidad_piezas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_defecto", nullable = false)
    private TipoDeDefecto tipoDefecto;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "idPieza", referencedColumnName = "idPieza")
//    private Pieza pieza;
}
