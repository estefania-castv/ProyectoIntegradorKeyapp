package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

// idUsuario solo lo usa el administrador para indicar a qué conversación responde.
public record MensajeSoporteRequest(@Schema(description = "Solo el administrador: usuario al que responde", example = "1") Integer idUsuario, @Schema(description = "Texto del mensaje (máximo 500 caracteres)", example = "No me abre la puerta del aula 301") String texto) {}
