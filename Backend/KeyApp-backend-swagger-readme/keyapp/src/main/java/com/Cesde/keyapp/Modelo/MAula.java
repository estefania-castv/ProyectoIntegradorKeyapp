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
    @Table(name = "aula")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class MAula {

        @Schema(description = "Identificador del aula", example = "1")
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idAula;

        @Schema(description = "Nombre del aula", example = "Aula 301")
        @Column(length = 50, nullable = false)
        private String nombre;

        @Schema(description = "Ubicación del aula", example = "Bloque A, piso 3")
        @Column(length = 100)
        private String ubicacion;

        @Schema(description = "Capacidad de personas", example = "30")
        @Column(nullable = false)
        private Integer capacidad;

        // Valores esperados: DISPONIBLE, MANTENIMIENTO, INACTIVA
        @Schema(description = "Estado: DISPONIBLE, MANTENIMIENTO, SIN_ENERGIA, PROBLEMAS_DE_RED, FUERA_DE_SERVICIO o INACTIVA", example = "DISPONIBLE")
        @Column(length = 20, nullable = false)
        private String estado;

        @Schema(description = "Facultad a la que pertenece el aula")
        @ManyToOne
        @JoinColumn(name = "id_facultad", nullable = false)
        private MFacultad facultad;

        @OneToOne(mappedBy = "aula")
        @JsonIgnore
        private MCerradura cerradura;

        @OneToMany(mappedBy = "aula")
        @JsonIgnore
        private List<MHorarioAcademico> horarios;

        @OneToMany(mappedBy = "aula")
        @JsonIgnore
        private List<MReserva> reservas;

        @OneToMany(mappedBy = "aula")
        @JsonIgnore
        private List<MObservacion> observaciones;

        public MAula(String nombre, String ubicacion, Integer capacidad, String estado, MFacultad facultad) {
            this.nombre = nombre;
            this.ubicacion = ubicacion;
            this.capacidad = capacidad;
            this.estado = estado;
            this.facultad = facultad;
        }
    }