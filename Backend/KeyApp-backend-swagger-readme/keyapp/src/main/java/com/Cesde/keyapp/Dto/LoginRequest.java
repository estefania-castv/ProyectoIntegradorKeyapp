package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(@Schema(description = "Correo electrónico del usuario", example = "ana.perez@keyapp.com") String email, @Schema(description = "Contraseña del usuario", example = "tu-contraseña") String password) {}
