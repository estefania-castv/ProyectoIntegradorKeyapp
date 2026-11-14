package com.Cesde.keyapp.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MUsuario {

    @Schema(description = "Identificador del usuario", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Schema(description = "Nombre completo del usuario", example = "Ana Pérez")
    @Column(length = 100, nullable = false)
    private String nombre;

    @Schema(description = "Correo electrónico (único), se usa para iniciar sesión", example = "ana.perez@keyapp.com")
    @Column(length = 150, nullable = false, unique = true)
    private String email;

    // Se puede recibir (POST/PUT) pero nunca se devuelve en las respuestas
    @Schema(description = "Contraseña cifrada con BCrypt (solo se recibe, nunca se devuelve)", example = "$2a$10$...")
    @Column(length = 255, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    // Valores esperados: PROFESOR o ADMINISTRADOR
    @Schema(description = "Rol: PROFESOR o ADMINISTRADOR", example = "PROFESOR")
    @Column(length = 20, nullable = false)
    private String rol;

    @Schema(description = "Facultad a la que pertenece el usuario")
    @ManyToOne
    @JoinColumn(name = "id_facultad", nullable = false)
    private MFacultad facultad;

    @OneToMany(mappedBy = "profesor")
    @JsonIgnore
    private List<MHorarioAcademico> horarios;

    @OneToMany(mappedBy = "profesor")
    @JsonIgnore
    private List<MReserva> reservas;

    @OneToMany(mappedBy = "administrador")
    @JsonIgnore
    private List<MObservacion> observaciones;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<MNotificacion> notificaciones;

    @OneToMany(mappedBy = "profesorDestino")
    @JsonIgnore
    private List<MTransferencia> transferenciasRecibidas;

    public MUsuario(String nombre, String email, String passwordHash, String rol, MFacultad facultad) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.facultad = facultad;
    }
}