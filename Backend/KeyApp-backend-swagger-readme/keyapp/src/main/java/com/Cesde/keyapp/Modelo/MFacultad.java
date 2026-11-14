package com.Cesde.keyapp.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "facultad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MFacultad {

    @Schema(description = "Identificador de la facultad", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFacultad;

    @Schema(description = "Nombre de la facultad", example = "Ingeniería")
    @Column(length = 100, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "facultad")
    @JsonIgnore
    private List<MUsuario> usuarios;

    @OneToMany(mappedBy = "facultad")
    @JsonIgnore
    private List<MAula> aulas;

    public MFacultad(String nombre) {
        this.nombre = nombre;
    }
}