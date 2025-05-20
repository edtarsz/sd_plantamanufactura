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

/*
 * Entidad que representa un defecto detectado en una pieza dentro del sistema.
 *
 * Esta clase se mapea a la tabla "defecto" en la base de datos y almacena
 * información detallada sobre un defecto específico, incluyendo su descripción,
 * costo asociado, cantidad de piezas afectadas y relaciones con el tipo de defecto
 * y la pieza correspondiente.
 *
 *
 * Se utilizan anotaciones de Lombok para generar automáticamente métodos como
 * getters, setters, constructores y el patrón builder.
 *
 * Esta clase es utilizada por servicios, controladores y repositorios para
 * manejar la lógica y persistencia de defectos en el sistema.
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

    /**
     * Identificador único del defecto. Se genera automáticamente al insertar un
     * nuevo registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDefecto")
    private Long idDefecto;

    /**
     * Descripción detallada del defecto. Campo obligatorio.
     */
    @Column(name = "detalles", nullable = false)
    private String detalles;

    /**
     * Costo asociado al defecto. Campo obligatorio.
     */
    @Column(name = "costo", nullable = false)
    private Double costo;

    /**
     * Cantidad de piezas afectadas por el defecto. Campo obligatorio.
     */
    @Column(name = "cantidad_piezas", nullable = false)
    private int cantidad_piezas;

    /**
     * Relación ManyToOne con TipoDefecto. Representa el tipo de defecto
     * asociado a este registro.
     */
    @ManyToOne
    @JoinColumn(name = "idTipoDefecto")
    private TipoDefecto tipoDefecto;

    /**
     * Relación ManyToOne con Pieza. Representa la pieza relacionada con el
     * defecto.
     */
    @ManyToOne
    @JoinColumn(name = "idPieza")
    private Pieza pieza;
}
