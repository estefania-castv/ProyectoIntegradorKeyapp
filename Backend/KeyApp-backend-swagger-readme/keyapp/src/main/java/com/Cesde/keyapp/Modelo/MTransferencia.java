package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transferencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MTransferencia {

    @Schema(description = "Identificador de la transferencia", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTransferencia;

    @Schema(description = "Fecha y hora de la solicitud", example = "2026-11-12T09:30:00")
    @Column(nullable = false)
    private LocalDateTime fechaTransferencia;

    @Schema(description = "Motivo de la transferencia", example = "Tengo una reunión a esa hora")
    @Column(length = 255)
    private String motivo;

    // Valores esperados: PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA (el origen la reemplazó), EXPIRADA
    @Schema(description = "Estado: PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA o EXPIRADA", example = "PENDIENTE")
    @Column(length = 20, nullable = false)
    private String estado;

    // Cuándo respondió el profesor destino (aceptar o rechazar)
    @Schema(description = "Fecha y hora en que respondió el profesor destino")
    private LocalDateTime fechaRespuesta;

    @Schema(description = "Reserva que se transfiere")
    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private MReserva reserva;

    @Schema(description = "Profesor que recibe la reserva")
    @ManyToOne
    @JoinColumn(name = "id_profesor_destino", nullable = false)
    private MUsuario profesorDestino;

    // Quien transfiere. Es null en las transferencias antiguas (anteriores a este cambio).
    @Schema(description = "Profesor que transfiere (nulo en transferencias antiguas)")
    @ManyToOne
    @JoinColumn(name = "id_profesor_origen")
    private MUsuario profesorOrigen;

    public MTransferencia(LocalDateTime fechaTransferencia, String motivo, MReserva reserva, MUsuario profesorDestino) {
        this.fechaTransferencia = fechaTransferencia;
        this.motivo = motivo;
        this.reserva = reserva;
        this.profesorDestino = profesorDestino;
        this.estado = "PENDIENTE";
    }
}
