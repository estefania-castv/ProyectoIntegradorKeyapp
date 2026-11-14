package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MTransferencia;
import com.Cesde.keyapp.Servicio.STransferencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Consulta de transferencias", description = "Consulta y eliminación del historial de transferencias de reservas.")
@RestController
@RequestMapping("/api/transferencias")
public class CTransferencia {

    @Autowired
    private STransferencia sTransferencia;

    @Operation(summary = "Listar transferencias", description = "Devuelve todas las transferencias.")
    @GetMapping
    public List<MTransferencia> listar() {
        return sTransferencia.listar();
    }

    @Operation(summary = "Consultar una transferencia por llave primaria", description = "Devuelve la transferencia según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MTransferencia> buscarPorId(@PathVariable Integer id) {
        return sTransferencia.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Las transferencias se crean desde POST /api/reservas/{id}/transferir (quedan PENDIENTES hasta que
    // el profesor destino las acepte), por eso aquí ya no hay un POST que cambie la reserva directamente.

    @Operation(summary = "Eliminar una transferencia", description = "Elimina la transferencia por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sTransferencia.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
