package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.TransferirRequest;
import com.Cesde.keyapp.Modelo.MTransferencia;
import com.Cesde.keyapp.Mapper.TransferenciaMapper;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import com.Cesde.keyapp.Servicio.STransferirReserva;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Transferir reservas", description = "Permite al profesor transferir una de sus reservas a otro profesor.")
@RestController
@RequestMapping("/api/reservas")
public class CTransferir {

    private final STransferirReserva transferirService;

    public CTransferir(STransferirReserva transferirService) {
        this.transferirService = transferirService;
    }

    // El usuario que transfiere sale del token; req.idUsuario() del cuerpo se ignora.
    @Operation(summary = "Transferir una reserva", description = "Crea una solicitud PENDIENTE hacia el profesor destino; la reserva sigue siendo del origen hasta que la acepte.")
    @PostMapping("/{id}/transferir")
    public ResponseEntity<?> transferir(@PathVariable Integer id,
                                        @RequestBody TransferirRequest req,
                                        @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            MTransferencia t = transferirService.transferir(
                    id, usuario.idUsuario(), req.idProfesorDestino(), req.motivo());
            return ResponseEntity.ok(TransferenciaMapper.toResponse(t));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
