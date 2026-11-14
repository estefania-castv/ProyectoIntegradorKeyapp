package com.Cesde.keyapp.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TransferenciaDetalleResponse(@Schema(description = "Identificador de la transferencia", example = "1") Integer idTransferencia, @Schema(description = "Identificador de la reserva", example = "1") Integer idReserva, @Schema(description = "Nombre del aula", example = "Aula 301") String aula,
                                           @Schema(description = "Fecha de la reserva", example = "2026-11-20") LocalDate fecha, @Schema(description = "Hora de inicio", example = "08:00:00") LocalTime horaInicio, @Schema(description = "Hora de fin", example = "10:00:00") LocalTime horaFin,
                                           @Schema(description = "Identificador del profesor que transfiere", example = "1") Integer idOrigen, @Schema(description = "Nombre del profesor que transfiere", example = "Ana Pérez") String origen,
                                           @Schema(description = "Identificador del profesor destino", example = "2") Integer idDestino, @Schema(description = "Nombre del profesor destino", example = "Luis Gómez") String destino,
                                           @Schema(description = "Motivo de la transferencia", example = "Tengo una reunión a esa hora") String motivo, @Schema(description = "Estado: PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA o EXPIRADA", example = "PENDIENTE") String estado,
                                           @Schema(description = "Fecha y hora de la solicitud", example = "2026-11-12T09:30:00") LocalDateTime fechaTransferencia, @Schema(description = "Fecha y hora de la respuesta del destino") LocalDateTime fechaRespuesta) {}
