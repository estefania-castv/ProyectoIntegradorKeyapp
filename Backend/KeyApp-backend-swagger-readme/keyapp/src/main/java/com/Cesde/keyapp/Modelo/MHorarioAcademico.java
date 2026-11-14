package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "horario_academico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MHorarioAcademico {

    @Schema(description = "Identificador del horario", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHorario;

    @Schema(description = "Día de la semana", example = "LUNES")
    @Column(length = 15, nullable = false)
    private String diaSemana;

    @Schema(description = "Hora de inicio de la clase", example = "08:00:00")
    @Column(nullable = false)
    private LocalTime horaInicio;

    @Schema(description = "Hora de fin de la clase", example = "10:00:00")
    @Column(nullable = false)
    private LocalTime horaFin;

    @Schema(description = "Periodo académico", example = "2026-1")
    @Column(length = 20, nullable = false)
    private String periodo;

    @Schema(description = "Aula donde se dicta la clase")
    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = false)
    private MAula aula;

    @Schema(description = "Profesor que dicta la clase")
    @ManyToOne
    @JoinColumn(name = "id_profesor", nullable = false)
    private MUsuario profesor;

    public MHorarioAcademico(String diaSemana, LocalTime horaInicio, LocalTime horaFin, String periodo,
                             MAula aula, MUsuario profesor) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.periodo = periodo;
        this.aula = aula;
        this.profesor = profesor;
    }
}