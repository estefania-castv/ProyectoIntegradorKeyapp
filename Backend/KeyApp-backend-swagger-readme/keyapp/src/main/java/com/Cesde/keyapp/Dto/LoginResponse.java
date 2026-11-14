package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(@Schema(description = "Identificador del usuario", example = "1") Integer idUsuario, @Schema(description = "Nombre del usuario", example = "Ana Pérez") String nombre, @Schema(description = "Correo electrónico", example = "ana.perez@keyapp.com") String email, @Schema(description = "Rol: PROFESOR o ADMINISTRADOR", example = "PROFESOR") String rol, @Schema(description = "Token JWT para el encabezado Authorization: Bearer", example = "eyJhbGciOiJIUzI1NiJ9...") String token) {}
