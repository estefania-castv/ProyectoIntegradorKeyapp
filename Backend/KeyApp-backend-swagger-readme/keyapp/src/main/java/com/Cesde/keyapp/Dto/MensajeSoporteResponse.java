package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MensajeSoporteResponse(@Schema(description = "Identificador del mensaje", example = "1") Integer idMensaje, @Schema(description = "Texto del mensaje", example = "No me abre la puerta del aula 301") String texto, @Schema(description = "Fecha y hora del mensaje", example = "2026-11-12T09:30:00") LocalDateTime fecha,
                                     @Schema(description = "Identificador de quien escribió", example = "1") Integer idAutor, @Schema(description = "Nombre de quien escribió", example = "Ana Pérez") String autor, @Schema(description = "Rol de quien escribió", example = "PROFESOR") String rol) {}
