package com.Cesde.keyapp.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cerradura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MCerradura {

    @Schema(description = "Identificador de la cerradura", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCerradura;

    @Schema(description = "Código único del dispositivo", example = "LOCK-001")
    @Column(length = 50, nullable = false, unique = true)
    private String codigoDispositivo;

    // Valores esperados: ACTIVA, INACTIVA, FALLA
    @Schema(description = "Estado: ACTIVA, INACTIVA o FALLA", example = "ACTIVA")
    @Column(length = 20, nullable = false)
    private String estado;

    @Schema(description = "Fecha de instalación", example = "2025-01-15")
    @Column(nullable = false)
    private LocalDate fechaInstalacion;

    // Relacion 1 a 1: cada aula tiene una unica cerradura
    @Schema(description = "Aula en la que está instalada (relación 1 a 1)")
    @OneToOne
    @JoinColumn(name = "id_aula", nullable = false, unique = true)
    private MAula aula;

    @OneToMany(mappedBy = "cerradura")
    @JsonIgnore
    private List<MRegistroAcceso> registrosAcceso;

    public MCerradura(String codigoDispositivo, String estado, LocalDate fechaInstalacion, MAula aula) {
        this.codigoDispositivo = codigoDispositivo;
        this.estado = estado;
        this.fechaInstalacion = fechaInstalacion;
        this.aula = aula;
    }
}