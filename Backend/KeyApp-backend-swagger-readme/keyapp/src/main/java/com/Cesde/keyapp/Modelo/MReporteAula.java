package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Novedad que un profesor informa sobre un aula (equipos dañados, sin red, etc.). La lee el administrador.
@Entity
@Table(name = "reporte_aula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MReporteAula {

    @Schema(description = "Identificador del reporte", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReporte;

    // Valores esperados: EQUIPOS, MOBILIARIO, ENERGIA, RED, LIMPIEZA, OTRO
    @Schema(description = "Tipo: EQUIPOS, MOBILIARIO, ENERGIA, RED, LIMPIEZA u OTRO", example = "EQUIPOS")
    @Column(length = 30, nullable = false)
    private String tipo;

    @Schema(description = "Descripción de la novedad (máximo 500 caracteres)", example = "El televisor no enciende")
    @Column(length = 500, nullable = false)
    private String descripcion;

    // Valores esperados: ABIERTO, RESUELTO
    @Schema(description = "Estado: ABIERTO o RESUELTO", example = "ABIERTO")
    @Column(length = 20, nullable = false)
    private String estado;

    @Schema(description = "Fecha y hora del reporte", example = "2026-11-12T09:30:00")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Schema(description = "Fecha y hora en que se resolvió")
    private LocalDateTime fechaResolucion;

    @Schema(description = "Aula reportada")
    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = false)
    private MAula aula;

    @Schema(description = "Profesor que informa la novedad")
    @ManyToOne
    @JoinColumn(name = "id_profesor", nullable = false)
    private MUsuario profesor;

    public MReporteAula(String tipo, String descripcion, String estado, LocalDateTime fecha,
                        MAula aula, MUsuario profesor) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
        this.aula = aula;
        this.profesor = profesor;
    }
}
