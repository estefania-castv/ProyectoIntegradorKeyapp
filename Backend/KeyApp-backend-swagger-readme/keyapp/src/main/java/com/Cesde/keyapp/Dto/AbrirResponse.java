package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AbrirResponse(@Schema(description = "Indica si la puerta se abrió", example = "true") boolean abierta, @Schema(description = "Mensaje con el resultado de la apertura", example = "Puerta abierta") String mensaje) {}
