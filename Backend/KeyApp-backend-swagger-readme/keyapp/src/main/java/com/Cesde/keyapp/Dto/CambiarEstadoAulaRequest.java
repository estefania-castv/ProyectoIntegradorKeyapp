package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CambiarEstadoAulaRequest(@Schema(description = "Nuevo estado: DISPONIBLE, MANTENIMIENTO, SIN_ENERGIA, PROBLEMAS_DE_RED, FUERA_DE_SERVICIO o INACTIVA", example = "MANTENIMIENTO") String estado) {}
