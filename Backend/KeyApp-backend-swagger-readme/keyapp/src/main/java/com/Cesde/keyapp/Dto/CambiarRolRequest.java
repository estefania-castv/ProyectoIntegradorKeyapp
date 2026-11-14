package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CambiarRolRequest(@Schema(description = "Nuevo rol: PROFESOR o ADMINISTRADOR", example = "ADMINISTRADOR") String rol) {}
