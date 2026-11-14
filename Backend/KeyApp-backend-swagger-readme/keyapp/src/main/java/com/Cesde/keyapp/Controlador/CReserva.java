package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import com.Cesde.keyapp.Servicio.SReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Gestión de reservas", description = "Consulta y administración de las reservas de aulas.")
@RestController
@RequestMapping("/api/reservas")
public class CReserva {

    @Autowired
    private SReserva sReserva;

    private boolean puedeVer(MReserva r, UsuarioAutenticado u) {
        return u.esAdmin() || (r.getProfesor() != null && r.getProfesor().getIdUsuario().equals(u.idUsuario()));
    }

    // El administrador ve todas las reservas; el profesor solo las suyas.
    @Operation(summary = "Listar reservas", description = "El administrador ve todas las reservas; el profesor solo las suyas.")
    @GetMapping
    public List<MReserva> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        List<MReserva> todas = sReserva.listar();
        if (usuario.esAdmin()) {
            return todas;
        }
        return todas.stream().filter(r -> puedeVer(r, usuario)).toList();
    }

    @Operation(summary = "Consultar una reserva por llave primaria", description = "Devuelve la reserva si el usuario es el dueño o administrador; 403 si no; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MReserva> buscarPorId(@PathVariable Integer id,
                                                @AuthenticationPrincipal UsuarioAutenticado usuario) {
        MReserva reserva = sReserva.buscarPorId(id).orElse(null);
        if (reserva == null) {
            return ResponseEntity.notFound().build();
        }
        if (!puedeVer(reserva, usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Adicionar una reserva", description = "Solo ADMINISTRADOR. Valida traslapes con otras reservas y con el horario académico; 409 si hay conflicto.")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MReserva reserva,
                                   @AuthenticationPrincipal UsuarioAutenticado usuario) {
        // Un profesor solo puede reservar a su nombre, sin importar lo que venga en la petición.
        if (!usuario.esAdmin()) {
            MUsuario yo = new MUsuario();
            yo.setIdUsuario(usuario.idUsuario());
            reserva.setProfesor(yo);
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sReserva.crear(reserva));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Solo ADMINISTRADOR (lo exige ConfiguracionSeguridad)
    @Operation(summary = "Actualizar una reserva", description = "Solo ADMINISTRADOR. Modifica una reserva existente.")
    @PutMapping("/{id}")
    public ResponseEntity<MReserva> actualizar(@PathVariable Integer id, @RequestBody MReserva reserva) {
        if (sReserva.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reserva.setIdReserva(id);
        return ResponseEntity.ok(sReserva.actualizar(reserva));
    }

    // Endpoint especifico de negocio: cancelar en lugar de borrar (dueño o administrador)
    @Operation(summary = "Cancelar una reserva", description = "Cancela la reserva en lugar de borrarla (dueño o administrador).")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id,
                                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        MReserva reserva = sReserva.buscarPorId(id).orElse(null);
        if (reserva == null) {
            return ResponseEntity.notFound().build();
        }
        if (!puedeVer(reserva, usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        sReserva.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    // Solo ADMINISTRADOR (lo exige ConfiguracionSeguridad)
    @Operation(summary = "Eliminar una reserva", description = "Solo ADMINISTRADOR. Elimina la reserva por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sReserva.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
