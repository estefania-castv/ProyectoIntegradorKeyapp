package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Mensaje de la ventana de soporte. Cada usuario tiene UNA conversación con los administradores:
// "usuario" es el dueño de la conversación y "autor" quien escribió el mensaje (el mismo usuario o un administrador).
@Entity
@Table(name = "mensaje_soporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MMensajeSoporte {

    @Schema(description = "Identificador del mensaje", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMensaje;

    @Schema(description = "Texto del mensaje (máximo 500 caracteres)", example = "No me abre la puerta del aula 301")
    @Column(length = 500, nullable = false)
    private String texto;

    @Schema(description = "Fecha y hora del mensaje", example = "2026-11-12T09:30:00")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Schema(description = "Dueño de la conversación de soporte")
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private MUsuario usuario;

    @Schema(description = "Quien escribió el mensaje (el usuario o un administrador)")
    @ManyToOne
    @JoinColumn(name = "id_autor", nullable = false)
    private MUsuario autor;

    public MMensajeSoporte(String texto, LocalDateTime fecha, MUsuario usuario, MUsuario autor) {
        this.texto = texto;
        this.fecha = fecha;
        this.usuario = usuario;
        this.autor = autor;
    }
}
