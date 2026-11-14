package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TransferirRequest(@Schema(description = "Se ignora: el usuario que transfiere sale del token", example = "1") Integer idUsuario, @Schema(description = "Identificador del profesor que recibirá la reserva", example = "2") Integer idProfesorDestino, @Schema(description = "Motivo de la transferencia", example = "Tengo una reunión a esa hora") String motivo) {}