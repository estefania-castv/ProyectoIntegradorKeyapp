package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.MensajeSoporteRequest;
import com.Cesde.keyapp.Mapper.MensajeSoporteMapper;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import com.Cesde.keyapp.Servicio.SSoporte;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

// Ventana de soporte: cada usuario escribe a los administradores y ellos le responden.
@Tag(name = "Ventana de soporte", description = "Cada usuario escribe a los administradores sus inconvenientes y ellos le responden.")
@RestController
@RequestMapping("/api/soporte")
public class CMensajeSoporte {

    private final SSoporte sSoporte;

    public CMensajeSoporte(SSoporte sSoporte) {
        this.sSoporte = sSoporte;
    }

    // Profesor: su conversación. Administrador: la del usuario indicado en ?idUsuario=
    @Operation(summary = "Listar los mensajes de una conversación", description = "Profesor: su propia conversación. Administrador: la del usuario indicado en ?idUsuario=.")
    @GetMapping("/mensajes")
    public ResponseEntity<?> mensajes(@RequestParam(required = false) Integer idUsuario,
                                      @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            return ResponseEntity.ok(sSoporte.mensajes(usuario, idUsuario).stream()
                    .map(MensajeSoporteMapper::toResponse)
                    .toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @Operation(summary = "Enviar un mensaje de soporte", description = "Registra un mensaje en la conversación del usuario (el administrador indica a quién responde con idUsuario).")
    @PostMapping("/mensajes")
    public ResponseEntity<?> enviar(@RequestBody MensajeSoporteRequest req,
                                    @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            var guardado = sSoporte.enviar(usuario, req.idUsuario(), req.texto());
            return ResponseEntity.status(HttpStatus.CREATED).body(MensajeSoporteMapper.toResponse(guardado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Solo ADMINISTRADOR
    @Operation(summary = "Listar las conversaciones de soporte", description = "Solo ADMINISTRADOR. Devuelve el último mensaje de cada conversación.")
    @GetMapping("/conversaciones")
    public ResponseEntity<?> conversaciones(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            return ResponseEntity.ok(sSoporte.conversaciones(usuario));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
