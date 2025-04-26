/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.repositorio;

import com.example.plantamanufactura.entidades.Pieza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ramos
 */
@Repository
public interface PiezaRepository extends JpaRepository<Pieza, Long> {
    //  Spring crea funciones para trabajar con la base de datos:
    //  findAll()
    //  findById(Long id) 
    //  save(Usuario usuario) 
    //  deleteById(Long id)
}
