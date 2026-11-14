package com.Cesde.keyapp.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MNotificacion {

    @Schema(description = "Identificador de la notificación", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotificacion;

    // Ej: recordatorio, alerta, aviso
    @Schema(description = "Tipo: recordatorio, alerta o aviso", example = "recordatorio")
    @Column(length = 30, nullable = false)
    private String tipo;

    @Schema(description = "Contenido de la notificación", example = "Su reserva inicia en 15 minutos")
    @Column(length = 255, nullable = false)
    private String mensaje;

    @Schema(description = "Fecha y hora de envío", example = "2026-11-12T09:30:00")
    @Column(nullable = false)
    private LocalDateTime fechaEnvio;

    @Schema(description = "Indica si ya fue leída", example = "false")
    @Column(nullable = false)
    private Boolean leido;

    @Schema(description = "Usuario destinatario")
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private MUsuario usuario;

    public MNotificacion(String tipo, String mensaje, LocalDateTime fechaEnvio, Boolean leido, MUsuario usuario) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
        this.usuario = usuario;
    }
}