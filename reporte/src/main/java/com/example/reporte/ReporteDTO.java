/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import com.example.reporte.defectos.DefectoDTO;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Objeto de transferencia de datos (DTO) para la entidad Reporte.
 *
 * Esta clase se utiliza para transferir información relevante de un reporte
 * entre capas de la aplicación, especialmente entre el backend y el frontend,
 * sin exponer directamente la entidad JPA.
 *
 * Contiene datos básicos del reporte como su identificador, lote asociado,
 * inspector responsable, costo total calculado, fecha del reporte, y una lista
 * de defectos relacionados representados como DefectoDTO.
 *
 * Utiliza anotaciones Lombok para generar automáticamente los métodos
 * getters, setters, constructores sin y con argumentos.
 *
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {

    /**
     * Identificador único del reporte.
     */
    private Long idReporte;

    /**
     * Identificador del lote asociado al reporte.
     */
    private String loteId;

    /**
     * Nombre del inspector que realizó el reporte.
     */
    private String inspector;

    /**
     * Costo total calculado del reporte, sumando costos de defectos y piezas.
     */
    private Double costoTotal;

    /**
     * Fecha en que se realizó el reporte.
     */
    private Date fecha;

    /**
     * Lista de defectos asociados al reporte, representados como objetos DTO.
     */
    private List<DefectoDTO> defectos;
}
