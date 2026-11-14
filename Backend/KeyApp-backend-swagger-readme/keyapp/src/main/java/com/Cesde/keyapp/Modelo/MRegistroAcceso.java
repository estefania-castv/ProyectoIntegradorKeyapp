package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_acceso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MRegistroAcceso {

    @Schema(description = "Identificador del registro", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRegistro;

    @Schema(description = "Fecha y hora del evento", example = "2026-11-12T08:05:00")
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    // Valores esperados: APERTURA, CIERRE, FALLIDO
    @Schema(description = "Tipo de evento: APERTURA, CIERRE o FALLIDO", example = "APERTURA")
    @Column(length = 20, nullable = false)
    private String tipoEvento;

    @Schema(description = "Resultado del intento", example = "ABIERTA")
    @Column(length = 50)
    private String resultado;

    // Pueden ser nulos: un intento fallido puede no tener reserva o cerradura validas
    @Schema(description = "Reserva asociada (puede ser nula en un intento fallido)")
    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private MReserva reserva;

    @Schema(description = "Cerradura involucrada (puede ser nula)")
    @ManyToOne
    @JoinColumn(name = "id_cerradura")
    private MCerradura cerradura;

    public MRegistroAcceso(LocalDateTime fechaHora, String tipoEvento, String resultado,
                           MReserva reserva, MCerradura cerradura) {
        this.fechaHora = fechaHora;
        this.tipoEvento = tipoEvento;
        this.resultado = resultado;
        this.reserva = reserva;
        this.cerradura = cerradura;
    }
}