package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AbrirRequest(@Schema(description = "Identificador del usuario (se ignora: el usuario sale del token)", example = "1") Integer idUsuario) {}
