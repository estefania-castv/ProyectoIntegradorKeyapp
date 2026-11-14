package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Mapper.TransferenciaDetalleMapper;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import com.Cesde.keyapp.Servicio.SRespuestaTransferencia;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// Solicitudes de transferencia: el profesor destino las ve y las acepta o rechaza.
@Tag(name = "Respuesta a transferencias", description = "El profesor destino ve las solicitudes de transferencia y las acepta o rechaza.")
@RestController
@RequestMapping("/api/transferencias")
public class CRespuestaTransferencia {

    private final SRespuestaTransferencia sRespuesta;

    public CRespuestaTransferencia(SRespuestaTransferencia sRespuesta) {
        this.sRespuesta = sRespuesta;
    }

    // Solicitudes pendientes o rechazadas del usuario, recibidas y enviadas
    @Operation(summary = "Listar mis solicitudes de transferencia", description = "Pendientes y rechazadas del usuario, recibidas y enviadas.")
    @GetMapping("/mias")
    public List<?> mias(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return sRespuesta.mias(usuario.idUsuario()).stream()
                .map(TransferenciaDetalleMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Aceptar una transferencia", description = "La reserva pasa al profesor destino (estado TRANSFERIDA).")
    @PutMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptar(@PathVariable Integer id,
                                     @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            return ResponseEntity.ok(TransferenciaDetalleMapper.toResponse(
                    sRespuesta.aceptar(id, usuario.idUsuario())));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @Operation(summary = "Rechazar una transferencia", description = "La reserva se queda con quien la envió, que puede transferirla de nuevo.")
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Integer id,
                                      @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            return ResponseEntity.ok(TransferenciaDetalleMapper.toResponse(
                    sRespuesta.rechazar(id, usuario.idUsuario())));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
