/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.reporte;

import com.example.reporte.defectos.Defecto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Entidad que representa un reporte dentro del sistema.
 *
 * Esta clase se mapea a la tabla "reporte" en la base de datos y contiene la
 * información principal de un reporte, como su identificador, lote, inspector,
 * fecha, costo total, usuario asociado y los defectos relacionados.
 *
 * La fecha se asigna automáticamente al momento de persistir un nuevo registro,
 * mediante el método anotado con @PrePersist.
 *
 * Los defectos asociados están definidos como una relación OneToMany con cascada
 * para asegurar que las operaciones sobre el reporte afecten también a los defectos
 * relacionados, permitiendo manejo de creación, actualización y eliminación en cascada.
 *
 * Se usan anotaciones de Lombok para generar automáticamente getters, setters,
 * constructores sin y con argumentos, así como el patrón builder para facilitar
 * la creación de instancias.
 *
 * @author Ramos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reporte")
public class Reporte {

    /**
     * Identificador único del reporte, generado automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReporte")
    private Long idReporte;

    /**
     * Identificador del lote al que pertenece el reporte.
     */
    @Column(name = "loteId", nullable = false)
    private String loteId;

    /**
     * Nombre del inspector que realizó el reporte.
     */
    @Column(name = "inspector", nullable = false)
    private String inspector;

    /**
     * Fecha y hora en que se creó el reporte. Se asigna automáticamente al
     * persistir el registro.
     */
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    /**
     * Método que se ejecuta antes de persistir el reporte para asignar la fecha
     * actual automáticamente.
     */
    @PrePersist
    protected void onCreate() {
        fecha = new Date();
    }

    /**
     * Costo total acumulado del reporte.
     */
    @Column(name = "costoTotal", nullable = false)
    private Double costoTotal;

    /**
     * Identificador del usuario que generó el reporte.
     */
    @Column(name = "idUsuario", nullable = false)
    private Long idUsuario;

    /**
     * Lista de defectos asociados al reporte. Se usa cascada para propagar
     * operaciones y orphanRemoval para eliminar defectos huérfanos cuando se
     * quitan de la lista.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Defecto> defectos;
}
