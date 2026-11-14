package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MNotificacion;
import com.Cesde.keyapp.Servicio.SNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de notificaciones", description = "Servicios para enviar, consultar y marcar como leídas las notificaciones.")
@RestController
@RequestMapping("/api/notificaciones")
public class CNotificacion {

    @Autowired
    private SNotificacion sNotificacion;

    @Operation(summary = "Listar notificaciones", description = "Devuelve todas las notificaciones.")
    @GetMapping
    public List<MNotificacion> listar() {
        return sNotificacion.listar();
    }

    @Operation(summary = "Consultar una notificación por llave primaria", description = "Devuelve la notificación según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MNotificacion> buscarPorId(@PathVariable Integer id) {
        return sNotificacion.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Enviar una notificación", description = "Registra una nueva notificación para un usuario.")
    @PostMapping
    public ResponseEntity<MNotificacion> enviar(@RequestBody MNotificacion notificacion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sNotificacion.enviar(notificacion));
    }

    @Operation(summary = "Marcar una notificación como leída", description = "Cambia el estado de la notificación a leída.")
    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Integer id) {
        sNotificacion.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar una notificación", description = "Elimina la notificación por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sNotificacion.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
