package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReporteAulaResponse(@Schema(description = "Identificador del reporte", example = "1") Integer idReporte, @Schema(description = "Identificador del aula", example = "1") Integer idAula, @Schema(description = "Nombre del aula", example = "Aula 301") String aula, @Schema(description = "Tipo de novedad", example = "EQUIPOS") String tipo,
                                  @Schema(description = "Descripción de la novedad", example = "El televisor no enciende") String descripcion, @Schema(description = "Estado: ABIERTO o RESUELTO", example = "ABIERTO") String estado, @Schema(description = "Fecha y hora del reporte", example = "2026-11-12T09:30:00") LocalDateTime fecha,
                                  @Schema(description = "Fecha y hora de resolución (nula si sigue abierto)") LocalDateTime fechaResolucion, @Schema(description = "Identificador de quien reporta", example = "1") Integer idProfesor, @Schema(description = "Nombre de quien reporta", example = "Ana Pérez") String profesor) {}
