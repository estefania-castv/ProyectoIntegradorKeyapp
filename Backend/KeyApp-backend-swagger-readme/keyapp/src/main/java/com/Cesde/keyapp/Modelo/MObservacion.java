package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "observacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MObservacion {

    @Schema(description = "Identificador de la observación", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idObservacion;

    @Schema(description = "Descripción de la observación", example = "Proyector con falla")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Valores esperados: ABIERTA, RESUELTA
    @Schema(description = "Estado: ABIERTA o RESUELTA", example = "ABIERTA")
    @Column(length = 20, nullable = false)
    private String estado;

    @Schema(description = "Fecha y hora de registro", example = "2026-11-12T09:30:00")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Schema(description = "Aula observada")
    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = false)
    private MAula aula;

    @Schema(description = "Administrador que registra la observación")
    @ManyToOne
    @JoinColumn(name = "id_administrador", nullable = false)
    private MUsuario administrador;

    public MObservacion(String descripcion, String estado, LocalDateTime fecha, MAula aula, MUsuario administrador) {
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
        this.aula = aula;
        this.administrador = administrador;
    }
}