package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

// Estado de un aula en este instante. Si estado = RESERVADA, trae el profesor y el horario de la reserva en curso.
public record AulaEstadoResponse(@Schema(description = "Identificador del aula", example = "1") Integer idAula, @Schema(description = "Estado actual: DISPONIBLE, RESERVADA, MANTENIMIENTO, etc.", example = "DISPONIBLE") String estado, @Schema(description = "Profesor de la reserva en curso (solo si está RESERVADA)", example = "Ana Pérez") String profesor,
                                 @Schema(description = "Hora de inicio de la reserva en curso", example = "08:00:00") LocalTime horaInicio, @Schema(description = "Hora de fin de la reserva en curso", example = "10:00:00") LocalTime horaFin) {}
