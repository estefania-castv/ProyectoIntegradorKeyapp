package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.AbrirResponse;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import java.util.Map;
import com.Cesde.keyapp.Servicio.SAcceso;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Apertura de puertas", description = "Permite al profesor dueño de una reserva abrir (simular) la puerta del aula.")
@RestController
@RequestMapping("/api/reservas")
public class CAcceso {

    private final SAcceso accesoService;

    public CAcceso(SAcceso accesoService) {
        this.accesoService = accesoService;
    }

    // El usuario sale del token, no del cuerpo de la petición (así nadie puede hacerse pasar por otro).
    @Operation(summary = "Abrir la puerta de una reserva", description = "Solo el profesor dueño de la reserva, desde la hora de inicio y hasta 20 minutos después. El usuario se toma del token JWT.")
    @PostMapping("/{id}/abrir")
    public ResponseEntity<AbrirResponse> abrir(@PathVariable Integer id,
                                               @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            AbrirResponse resultado = accesoService.abrir(id, usuario.idUsuario());
            return ResponseEntity.status(resultado.abierta() ? 200 : 403).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AbrirResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AbrirResponse(false, "No fue posible procesar la apertura"));
        }
    }
}
