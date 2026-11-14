package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReporteAulaRequest(@Schema(description = "Identificador del aula reportada", example = "1") Integer idAula, @Schema(description = "Tipo: EQUIPOS, MOBILIARIO, ENERGIA, RED, LIMPIEZA u OTRO", example = "EQUIPOS") String tipo, @Schema(description = "Descripción de la novedad (máximo 500 caracteres)", example = "El televisor no enciende") String descripcion) {}
