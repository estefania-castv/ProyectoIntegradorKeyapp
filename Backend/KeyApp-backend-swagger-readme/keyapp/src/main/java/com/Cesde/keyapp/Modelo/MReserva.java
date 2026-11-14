package com.Cesde.keyapp.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MReserva {

    @Schema(description = "Identificador de la reserva", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @Schema(description = "Fecha de la reserva", example = "2026-11-20")
    @Column(nullable = false)
    private LocalDate fecha;

    @Schema(description = "Hora de inicio", example = "08:00:00")
    @Column(nullable = false)
    private LocalTime horaInicio;

    @Schema(description = "Hora de fin", example = "10:00:00")
    @Column(nullable = false)
    private LocalTime horaFin;

    // Valores esperados: ACTIVA, CANCELADA, MODIFICADA, TRANSFERIDA, FINALIZADA
    @Schema(description = "Estado: ACTIVA, CANCELADA, MODIFICADA, TRANSFERIDA o FINALIZADA", example = "ACTIVA")
    @Column(length = 20, nullable = false)
    private String estado;

    @Schema(description = "Fecha y hora en que se creó la reserva", example = "2026-11-12T09:30:00")
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Schema(description = "Aula reservada")
    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = false)
    private MAula aula;

    @Schema(description = "Profesor dueño de la reserva")
    @ManyToOne
    @JoinColumn(name = "id_profesor", nullable = false)
    private MUsuario profesor;

    @OneToMany(mappedBy = "reserva")
    @JsonIgnore
    private List<MRegistroAcceso> registrosAcceso;

    @OneToMany(mappedBy = "reserva")
    @JsonIgnore
    private List<MTransferencia> transferencias;

    public MReserva(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String estado,
                    LocalDateTime fechaCreacion, MAula aula, MUsuario profesor) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.aula = aula;
        this.profesor = profesor;
    }
}