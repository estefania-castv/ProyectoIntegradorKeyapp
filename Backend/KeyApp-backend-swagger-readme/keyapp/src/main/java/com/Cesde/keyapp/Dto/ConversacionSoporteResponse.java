package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ConversacionSoporteResponse(@Schema(description = "Identificador del dueño de la conversación", example = "1") Integer idUsuario, @Schema(description = "Nombre del usuario", example = "Ana Pérez") String nombre, @Schema(description = "Último mensaje de la conversación", example = "No me abre la puerta") String ultimoMensaje,
                                          @Schema(description = "Fecha y hora del último mensaje", example = "2026-11-12T09:30:00") LocalDateTime fecha) {}
